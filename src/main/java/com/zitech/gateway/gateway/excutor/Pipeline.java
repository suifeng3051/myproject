package com.zitech.gateway.gateway.excutor;

import com.zitech.gateway.gateway.Constants;
import com.zitech.gateway.gateway.model.RequestEvent;
import com.zitech.gateway.gateway.pipes.IPipe;
import com.zitech.gateway.gateway.pipes.impl.DispatchPipe;
import com.zitech.gateway.gateway.pipes.impl.ErrorPipe;
import com.zitech.gateway.gateway.pipes.impl.ParsePipe;
import com.zitech.gateway.gateway.pipes.impl.PostPipe;
import com.zitech.gateway.gateway.pipes.impl.ResultPipe;
import com.zitech.gateway.gateway.pipes.impl.ServicePipe;
import com.zitech.gateway.gateway.pipes.impl.SignPipe;
import com.zitech.gateway.gateway.pipes.impl.TokenPipe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class Pipeline {

    private static final Logger logger = LoggerFactory.getLogger(Pipeline.class);

    private static Pipeline pipeline = new Pipeline();
    private volatile boolean isStopping = false;

    private int preThreadCount;

    private volatile TimingExecutor preExecutor;
    private TimingExecutor postExecutor;

    private IPipe parsePipe = new ParsePipe();
    private IPipe tokenPipe = new TokenPipe();
    private IPipe signPipe = new SignPipe();
    private IPipe dispatchPipe = new DispatchPipe();
    private IPipe servicePipe = new ServicePipe();
    private IPipe resultPipe = new ResultPipe();
    private IPipe errorPipe = new ErrorPipe();
    private IPipe postPipe = new PostPipe();

    private AtomicInteger preCount = new AtomicInteger(0);

    private Pipeline() {

        int cpuCount = PerfMonitor.getCpuCount();

        preThreadCount = cpuCount * 2;

        preExecutor = TimingExecutor.newFixedThreadPool("pre", preThreadCount, preThreadCount, 60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(20000), false, preCount);
        postExecutor = TimingExecutor.newFixedThreadPool("post", cpuCount, cpuCount, 60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(20000), false);

        RejectedExecutionHandler rejectedExecutionHandler = (r, executor) -> {
            TimingExecutor timingExecutor = (TimingExecutor) executor;
            logger.error("an task has been rejected in  {}", timingExecutor.getName());
        };

        preExecutor.setRejectedExecutionHandler(rejectedExecutionHandler);
        postExecutor.setRejectedExecutionHandler(rejectedExecutionHandler);

        preExecutor.prestartAllCoreThreads();
        postExecutor.prestartAllCoreThreads();

    }

    public static Pipeline getInstance() {
        return pipeline;
    }

    public void process(RequestEvent event) {
        PipeWorker worker;
        switch (event.getState()) {
            case PARSE:
                worker = new PipeWorker(parsePipe, event, Constants.ST_PRE_PIPE);
                preExecutor.execute(worker);
                break;
            case TOKEN:
                tokenPipe.onEvent(event);
                break;
            case SIGN:
                signPipe.onEvent(event);
                break;
            case DISPATCH:
                dispatchPipe.onEvent(event);
                break;
            case ASYNC:
                servicePipe.onEvent(event);
                break;
            case RESULT:
                worker = new PipeWorker(resultPipe, event, Constants.ST_JAVA_RESULT);
                postExecutor.execute(worker);
                break;
            case POST:
                worker = new PipeWorker(postPipe, event, Constants.ST_POST);
                postExecutor.execute(worker);
                break;
            case ERROR:
                errorPipe.onEvent(event);
                break;
            case COMPLETE:
                break;
        }
    }

    /**
     * start pipeline
     */
    public void start() {
        isStopping = false;
    }

    /**
     * stop pipeline
     */
    public void stop() {
        isStopping = true;
        preExecutor.shutdown();
        postExecutor.shutdown();
    }

    public int getPreThreadCount() {
        return preThreadCount;
    }

    public int getPreTaskCount() {
        return preCount.get();
    }

    public long getPostTaskCount() {
        return postExecutor.getTaskCount();
    }

    public int getHttpAsyncTask() {
        return ServicePipe.getAsyncRequestCount().get();
    }

}
