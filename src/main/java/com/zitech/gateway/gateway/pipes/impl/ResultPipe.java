package com.zitech.gateway.gateway.pipes.impl;

import com.alibaba.fastjson.JSONObject;
import com.zitech.gateway.gateway.model.ApiResponse;
import com.zitech.gateway.gateway.model.RequestEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.async.DeferredResult;


@Service
@Pipe(Group = 2, Order = 1)
public class ResultPipe extends AbstractPipe {

    private static final Logger logger = LoggerFactory.getLogger(ResultPipe.class);

    public void onEvent(RequestEvent event) {

        DeferredResult<Object> eventResult = event.getResult();
        if (eventResult.isSetOrExpired()) {
            logger.warn("a request has been expired: " + event);
            return;
        }

        if (!StringUtils.isEmpty(event.getResultStr())) {
            ApiResponse apiResponse = JSONObject.parseObject(event.getResultStr(), ApiResponse.class);
            if (apiResponse.getCode() == 0) {
                ResponseEntity<String> responseEntity = new ResponseEntity<String>(event.getResultStr(), HttpStatus.valueOf(200));
                eventResult.setResult(responseEntity);
                logger.info("get correct response, event: {}, result: {}", event, event.getResultStr());
            } else {
                ResponseEntity<String> responseEntity = new ResponseEntity<String>(event.getResultStr(), HttpStatus.valueOf(200));
                eventResult.setResult(responseEntity);
                logger.info("get error response, event: {}, result: {}", event, event.getResultStr());
            }
        }
    }
}
