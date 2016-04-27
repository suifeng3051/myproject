package com.zitech.gateway.gateway.excutor;

import com.zitech.gateway.common.BaseException;
import com.zitech.gateway.gateway.Constants;
import com.zitech.gateway.gateway.model.RequestEvent;
import com.zitech.gateway.gateway.pipes.IPipe;
import com.zitech.gateway.gateway.pipes.impl.Pipe;
import com.zitech.gateway.utils.ClassUtils;
import com.zitech.gateway.utils.SpringContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class Pipeline {

    private static final Logger logger = LoggerFactory.getLogger(Pipeline.class);

    private static Pipeline instance = new Pipeline();

    private Map<Integer, List<IPipe>> groupPipes = new TreeMap<>();
    private Map<Integer, ThreadPoolExecutor> executors = new TreeMap<>();

    private Pipeline() {
        this.initialPipes();
        this.initialExecutors();
    }

    public void start() {
        //nothing to do
    }

    public void process(RequestEvent event) {
        Integer group = getNextGroup(event);
        if(group == null)
            return;

        executors.get(group).execute(() -> {
            if (event.getException() != null) {
                processException(event, event.getException());
                return;
            }

            List<IPipe> pipes = groupPipes.get(group);
            for (IPipe pipe : pipes) {
                try {
                    pipe.onEvent(event);
                } catch (Exception e) {
                    processException(event, e);
                    break;
                }
            }
        });
    }

    private void processException(RequestEvent event, Exception e) {
        DeferredResult<Object> deferredResult = event.getResult();
        if (deferredResult.isSetOrExpired()) {
            logger.error("an request expired: " + event);
            return;
        }

        String msg;
        if (e instanceof BaseException) {
            BaseException be = (BaseException) e;
            msg = String.format(Constants.ERROR_RESPONSE, be.getCode(), be.getDescription());
        } else {
            msg = String.format(Constants.ERROR_RESPONSE, -1, "unknown error: " + e.getMessage());
        }
        ResponseEntity<String> responseEntity = new ResponseEntity<>(msg, HttpStatus.valueOf(200));
        deferredResult.setResult(responseEntity);
    }

    private Integer getNextGroup(RequestEvent event) {
        Integer step = event.getStep();
        event.setStep(++step);

        int i = 0;
        for (Map.Entry<Integer, ThreadPoolExecutor> entry : executors.entrySet()) {
            if (step.equals(i)) {
                return entry.getKey();
            }
            ++i;
        }

        return null;
    }

    private void initialPipes() {
        TreeMap<Integer, TreeMap<Integer, IPipe>> groupMap = new TreeMap<>();
        List<Class> allPipes = ClassUtils.getAllClassByInterface(IPipe.class);
        for (Class clazz : allPipes) {
            Pipe pipe = (Pipe) clazz.getAnnotation(Pipe.class);
            if(pipe == null)
                continue;

            Integer group = pipe.Group();
            Integer order = pipe.Order();

            TreeMap<Integer, IPipe> pipeMap = null;
            if ((pipeMap = groupMap.get(group)) == null) {
                groupMap.put(group, new TreeMap<>());
                pipeMap = groupMap.get(group);
            }

            pipeMap.put(order, (IPipe) SpringContext.getBean(clazz));
        }

        for (Map.Entry<Integer, TreeMap<Integer, IPipe>> entry : groupMap.entrySet()) {
            List<IPipe> pipeList = entry.getValue().entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
            groupPipes.put(entry.getKey(), pipeList);
        }
    }

    private void initialExecutors() {
        int threadNum = getCpuCount() * 2;
        for (Map.Entry<Integer, List<IPipe>> entry : groupPipes.entrySet()) {
            Integer group = entry.getKey();
            ThreadPoolExecutor executor = new ThreadPoolExecutor(threadNum, threadNum, 60L, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(20000));
            executor.prestartAllCoreThreads();
            executor.setThreadFactory(new ThreadFactory() {
                private AtomicInteger count = new AtomicInteger(0);
                public Thread newThread(Runnable r) {
                    String name = String.format("Thread-Group-%d-%d", group, count.getAndDecrement());
                    Thread thread = new Thread();
                    thread.setName(name);
                    return thread;
                }
            });
            executors.put(group, executor);
        }
    }

    private int getCpuCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static Pipeline getInstance() {
        return instance;
    }

    public int getPreThreadCount() {
        return 0;
    }

    public int getPreTaskCount() {
        return 0;
    }

    public int getHttpAsyncTask() {
        return 0;
    }

}
