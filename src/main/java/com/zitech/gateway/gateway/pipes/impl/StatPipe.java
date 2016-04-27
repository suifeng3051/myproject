package com.zitech.gateway.gateway.pipes.impl;

import com.zitech.gateway.gateway.Constants;
import com.zitech.gateway.gateway.excutor.TicTac;
import com.zitech.gateway.gateway.model.RequestEvent;
import com.zitech.gateway.stat.constants.GatewayConstant;
import com.zitech.gateway.stat.service.MonitorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
@Pipe(Group = 2, Order = 2)
public class StatPipe extends AbstractPipe {

    private static final Logger logger = LoggerFactory.getLogger(StatPipe.class);

    @Autowired
    private MonitorService monitor;

    public void onEvent(RequestEvent event) {

        long all = 0;
        long pre = 0;
        long call = 0;

        TicTac ticTac = event.getTicTac();
        Map<String, Long> perfMap = new HashMap<>();

        for (Map.Entry<String, TicTac.STEntry> entry : ticTac.getEntryMap().entrySet()) {
           if (entry.getKey().equals(Constants.ST_ALL)) {
                all = entry.getValue().getElapsed();
            } else if (entry.getKey().equals(Constants.ST_CALL)) {
                call = entry.getValue().getElapsed();
            }
            perfMap.put(entry.getKey(), entry.getValue().getElapsed());
        }


        String isSuccess = GatewayConstant.TRUE;
        monitor.saveMonitorData(event, all, call, pre, 0, isSuccess, "");
    }
}
