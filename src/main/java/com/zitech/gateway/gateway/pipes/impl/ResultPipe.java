package com.zitech.gateway.gateway.pipes.impl;

import com.alibaba.fastjson.JSONObject;
import com.zitech.gateway.gateway.Util;
import com.zitech.gateway.gateway.exception.PipeException;
import com.zitech.gateway.gateway.model.ApiResponse;
import com.zitech.gateway.gateway.model.RequestEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.async.DeferredResult;


@Service
@Pipe(Group = 'B', Order = 1)
public class ResultPipe extends AbstractPipe {

    private static final Logger logger = LoggerFactory.getLogger(ResultPipe.class);

    public void onEvent(RequestEvent event) {

        DeferredResult<Object> eventResult = event.getResult();
        if (eventResult.isSetOrExpired()) {
            logger.warn("a request has been expired: " + event);
            return;
        }

        String result = event.getResultStr();
        if (!StringUtils.isEmpty(result)) {
            ApiResponse apiResponse = JSONObject.parseObject(result, ApiResponse.class);
            HttpHeaders headers = Util.getHeaders(event);
            if (apiResponse.getCode() == 0) {
                ResponseEntity<String> responseEntity = new ResponseEntity<>(result,
                        headers, HttpStatus.valueOf(200));
                eventResult.setResult(responseEntity);
                logger.info("get correct response, event: {}, result: {}", event, result);
            } else if (apiResponse.getCode() == -1) {
                ResponseEntity<String> responseEntity = new ResponseEntity<>(result,
                        headers, HttpStatus.valueOf(200));
                eventResult.setResult(responseEntity);
                logger.error("get error response, event: {}, result: {}", event, result);
            } else {
                ResponseEntity<String> responseEntity = new ResponseEntity<>(result,
                        headers, HttpStatus.valueOf(200));
                eventResult.setResult(responseEntity);
                logger.info("get unexpected response, event: {}, result: {}", event, result);
            }
        } else {
            throw new PipeException(5215, "没有获得结果");
        }
    }
}
