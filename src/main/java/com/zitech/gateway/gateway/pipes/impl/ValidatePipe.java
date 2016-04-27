package com.zitech.gateway.gateway.pipes.impl;

import com.zitech.gateway.gateway.cache.ParamCache;
import com.zitech.gateway.gateway.model.RequestEvent;
import com.zitech.gateway.param.Param;
import com.zitech.gateway.param.ParamHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Pipe(Group = 1, Order = 3)
public class ValidatePipe extends AbstractPipe {

    private static final Logger logger = LoggerFactory.getLogger(ValidatePipe.class);

    @Autowired
    private ParamCache paramCache;

    public void onEvent(RequestEvent event) {

        String body = event.getBody();
        Param param = paramCache.get(event.getId(), event.getApi().getId());
        ParamHelper.validate(body, param);
    }
}
