package com.zitech.gateway.gateway.pipes.impl;

import com.zitech.gateway.apiconfig.model.Api;
import com.zitech.gateway.gateway.Constants;
import com.zitech.gateway.gateway.PipeHelper;
import com.zitech.gateway.gateway.cache.AccessTokenCache;
import com.zitech.gateway.gateway.cache.ApiCache;
import com.zitech.gateway.gateway.model.RequestEvent;
import com.zitech.gateway.oauth.model.AccessToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;


@Service
@Pipe(Group = 'A', Order = 1)
public class ParsePipe extends AbstractPipe {

    private static final Logger logger = LoggerFactory.getLogger(ParsePipe.class);

    @Autowired
    private AccessTokenCache tokenCache;

    @Autowired
    private ApiCache apiCache;

    public void onEvent(RequestEvent event) throws Exception {

        HttpServletRequest request = event.getRequest();

        String tokenStr = request.getParameter(Constants.ACCESS_TOKEN);

        AccessToken token = tokenCache.get(tokenStr);
        Api api = apiCache.get(event.getNamespace(), event.getMethod(), event.getVersion());
        String ip = PipeHelper.getIp(request);

        event.setAccessToken(token);
        event.setApi(api);
        event.setIp(ip);
    }
}
