package com.zitech.gateway.gateway.excutor;

import com.zitech.gateway.common.BaseException;
import com.zitech.gateway.gateway.model.RequestEvent;
import com.zitech.gateway.gateway.pipes.IPipe;
import com.zitech.gateway.gateway.pipes.impl.Pipe;
import com.zitech.gateway.utils.ClassUtils;
import com.zitech.gateway.utils.SpringContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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

    }

    public void process(RequestEvent event) {
        for (Integer group : executors.keySet()) {
            List<IPipe> pipes = groupPipes.get(group);
            executors.get(group).execute(() -> {
                for (IPipe pipe : pipes) {
                    try {
                        pipe.onEvent(event);
                    } catch (BaseException e) {
                        logger.info("");
                    } catch (Exception e) {
                        logger.error("unknown exception", e);
                    }
                }
            });
        }
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
            ThreadPoolExecutor executor = new ThreadPoolExecutor(threadNum, threadNum, 60L, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(20000));
            executors.put(entry.getKey(), executor);
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
