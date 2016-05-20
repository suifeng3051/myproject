package com.zitech.gateway.gateway.pipes.impl;

import com.zitech.gateway.gateway.Constants;
import com.zitech.gateway.gateway.excutor.Pipeline;
import com.zitech.gateway.gateway.excutor.TicTac;
import com.zitech.gateway.gateway.model.RequestEvent;
import com.zitech.gateway.gateway.model.ServeResponse;
import com.zitech.gateway.monitor.service.impl.MonitorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Pipe(Group = 'B', Order = 2)
public class StatPipe extends AbstractPipe {

    private static final Logger logger = LoggerFactory.getLogger(StatPipe.class);

    @Autowired
    private MonitorService monitor;

    public void onEvent(RequestEvent event) {

        long all = 0;
        long call = 0;

        Pipeline pipeline = Pipeline.getInstance();
        if (pipeline.checkLastStep(event)) {
            Integer group = pipeline.getGroup(event);
            String groupName = String.format("group-%c", group);

            TicTac ticTac = event.getTicTac();
            ticTac.tac(groupName);

            ticTac.tac(Constants.ST_ALL);
        }

        TicTac ticTac = event.getTicTac();

        all = ticTac.elapsed(Constants.ST_ALL);
        call = ticTac.elapsed(Constants.ST_CALL);

        if (all > 0) {
            ServeResponse response = event.getServeResponse();
            String path = event.getNamespace() + "/" + event.getVersion() + "/" + event.getMethod();
            monitor.saveMonitorData(path, all, call, response.getCode(), ticTac.toString());
        }

        logger.info("performance of {}: {}", event.uuid, ticTac.toString());
    }
}
