package com.zitech.gateway.gateway.pipes.impl;

import com.alibaba.fastjson.JSONObject;
import com.zitech.gateway.exception.CarmenException;
import com.zitech.gateway.gateway.Constants;
import com.zitech.gateway.gateway.excutor.PerfMonitor;
import com.zitech.gateway.gateway.excutor.Pipeline;
import com.zitech.gateway.gateway.excutor.TicTac;
import com.zitech.gateway.gateway.model.RequestEvent;
import com.zitech.gateway.gateway.model.RequestState;
import com.zitech.gateway.monitor.constants.GatewayConstant;
import com.zitech.gateway.monitor.service.MonitorService;
import com.zitech.gateway.utils.SpringContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class PostPipe extends AbstractPipe {

    private static Logger logger = LoggerFactory.getLogger(PostPipe.class);

    private PerfMonitor perfMonitor;
    private MonitorService monitorService;

    public PostPipe() {
        this.perfMonitor = SpringContext.getBean(PerfMonitor.class);
        this.monitorService = SpringContext.getBean(MonitorService.class);
    }

    @Override
    public void onEvent(RequestEvent event) {

        long all = 0;
        long call = 0;
        long pre = 0;
        long redis = 0;
        String isSuccess = GatewayConstant.TRUE;
        String exceptionName = "";
        try {
            logger.debug("begin of post processing: {}", event);

            TicTac ticTac = event.getTicTac();

            Map<String, Long> perfMap = new HashMap<>();

            for (Map.Entry<String, TicTac.STEntry> entry : ticTac.getEntryMap().entrySet()) {
                if (entry.getKey().equals(Constants.ST_PRE_PIPE)) {
                    pre = entry.getValue().getElapsed();
                } else if (entry.getKey().equals(Constants.ST_REDIS_TOKEN)) {
                    redis = entry.getValue().getElapsed();
                } else if (entry.getKey().equals(Constants.ST_ALL)) {
                    all = entry.getValue().getElapsed();
                } else if (entry.getKey().equals(Constants.ST_CALL)) {
                    call = entry.getValue().getElapsed();
                }

                if (!entry.getKey().equals(Constants.ST_POST))
                    perfMap.put(entry.getKey(), entry.getValue().getElapsed());
            }

            // log unknown error
            Exception e = event.getException();
            if (e != null && !(e instanceof CarmenException)) {
                logger.error("unknown error", event.getException());
                isSuccess = GatewayConstant.FAILUE;
                exceptionName = "Exception";
            }

            logger.info("event: {}, performance of {}.{}: {}", event.getId(), event.getNamespace(), event.getMethod(), JSONObject.toJSON(perfMap));

        } finally {
            logger.debug("end of post processing: {}", event);

            monitorService.saveMonitorData(event, all, call, pre, redis, isSuccess, exceptionName);

            onNext(event);
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    protected void onNext(RequestEvent event) {
        event.setState(RequestState.COMPLETE);

        // go on
        Pipeline.getInstance().process(event);
    }
}
