package com.zitech.gateway.gateway.pipes.impl;

import com.zitech.gateway.apiconfig.model.Api;
import com.zitech.gateway.gateway.Constants;
import com.zitech.gateway.gateway.excutor.TicTac;
import com.zitech.gateway.gateway.model.RequestEvent;
import com.zitech.gateway.gateway.model.ServeResponse;
import com.zitech.gateway.monitor.service.MonitorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@Pipe(Group = 'B', Order = 2)
public class StatPipe extends AbstractPipe {

    private static final Logger logger = LoggerFactory.getLogger(StatPipe.class);

    @Autowired
    private MonitorService monitor;

    public void onEvent(RequestEvent event) {

        long all = 0;
        long call = 0;

        TicTac ticTac = event.getTicTac();
        Map<String, TicTac.STEntry> ticTacEntryMap = ticTac.getEntryMap();

        for (Map.Entry<String, TicTac.STEntry> entry : ticTacEntryMap.entrySet()) {
           if (entry.getKey().equals(Constants.ST_ALL)) {
                all = entry.getValue().getElapsed();
            } else if (entry.getKey().equals(Constants.ST_CALL)) {
                call = entry.getValue().getElapsed();
            }
        }

        String stat = ticTacEntryMap.toString();

        if (all > 0) {
            ServeResponse response = event.getServeResponse();
            String path = event.getNamespace() + "/" + event.getVersion() + "/" + event.getMethod();
            monitor.saveMonitorData(path, all, call, response.getCode(), stat);
        }

        logger.info("performance of {}: {}", event.uuid, stat);
    }
}
