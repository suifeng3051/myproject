package com.zitech.gateway.gateway.excutor;

import com.zitech.gateway.gateway.model.RequestEvent;

/**
 * Created by bobo on 4/26/16.
 */
public class Pipeline {
    private static Pipeline instance;
    private int preThreadCount;
    private int preTaskCount;
    private int httpAsyncTask;

    public static Pipeline getInstance() {
        return instance;
    }

    public int getPreThreadCount() {
        return preThreadCount;
    }

    public int getPreTaskCount() {
        return preTaskCount;
    }

    public int getHttpAsyncTask() {
        return httpAsyncTask;
    }

    public void start() {
    }

    public void process(RequestEvent event) {

    }
}
