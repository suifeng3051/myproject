package com.zitech.gateway.gateway.pipes.impl;

import com.zitech.gateway.apiconfig.model.Api;
import com.zitech.gateway.gateway.Constants;
import com.zitech.gateway.gateway.cache.AccessTokenCache;
import com.zitech.gateway.gateway.cache.ApiCache;
import com.zitech.gateway.gateway.model.RequestEvent;
import com.zitech.gateway.gateway.utils.IPUtil;
import com.zitech.gateway.oauth.model.AccessToken;
import com.zitech.gateway.utils.SpringContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;


public class ParsePipe extends AbstractPipe {

    private static final Logger logger = LoggerFactory.getLogger(ParsePipe.class);

    private AccessTokenCache tokenCache;
    private ApiCache apiCache;

    public ParsePipe() {
        tokenCache = SpringContext.getBean(AccessTokenCache.class);
        apiCache = SpringContext.getBean(ApiCache.class);
    }

    public void onEvent(RequestEvent event) {
        HttpServletRequest request = event.getRequest();

        String tokenStr = request.getParameter(Constants.ACCESS_TOKEN);
        AccessToken token = tokenCache.get(event.getId(), tokenStr);
        Api api = apiCache.get(event.getId(), event.getNamespace(), event.getMethod(), event.getVersion());
        String ip = IPUtil.getIp(request);

        event.setAccessToken(token);
        event.setApi(api);
        event.setIp(ip);
    }
}
