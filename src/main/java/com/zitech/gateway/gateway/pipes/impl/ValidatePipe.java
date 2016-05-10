package com.zitech.gateway.gateway.pipes.impl;

import com.zitech.gateway.apiconfig.model.Api;
import com.zitech.gateway.common.RequestType;
import com.zitech.gateway.gateway.cache.ParamCache;
import com.zitech.gateway.gateway.exception.TokenException;
import com.zitech.gateway.gateway.model.RequestEvent;
import com.zitech.gateway.oauth.model.AccessToken;
import com.zitech.gateway.param.Param;
import com.zitech.gateway.param.ParamHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Pipe(Group = 'A', Order = 2)
public class ValidatePipe extends AbstractPipe {

    private static final Logger logger = LoggerFactory.getLogger(ValidatePipe.class);

    @Autowired
    private ParamCache paramCache;

    public void onEvent(RequestEvent event) throws Exception {

        // param check
        if (event.getRequestType() == RequestType.POST) {
            String body = event.getBody();
            Param param = paramCache.get(event.getApi().getId());
            ParamHelper.validate(body, param);
        }

        // login check
        AccessToken token = event.getAccessToken();
        Api api = event.getApi();
        if (api.getLogin() == 1 && token.getUserId() == 0) {
            throw new TokenException(5218, "请先登陆");
        }
    }
}
