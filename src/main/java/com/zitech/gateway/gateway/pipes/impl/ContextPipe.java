package com.zitech.gateway.gateway.pipes.impl;

import com.zitech.gateway.gateway.Constants;
import com.zitech.gateway.gateway.cache.ClientCache;
import com.zitech.gateway.gateway.model.RequestEvent;
import com.zitech.gateway.oauth.model.AccessToken;
import com.zitech.gateway.oauth.model.Client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@Pipe(Group = 1, Order = 4)
public class ContextPipe extends AbstractPipe {

    private static final Logger logger = LoggerFactory.getLogger(ContextPipe.class);

    @Autowired
    private ClientCache clientCache;

    public void onEvent(RequestEvent event) {

        AccessToken token = event.getAccessToken();
        Client client = clientCache.get(event.getId(), token.getClientId());

        Map<String, String> contextMap = event.getContextMap();
        contextMap.put(Constants.PARAMS_ACCESS_TOKEN, token.getAccessToken());
        contextMap.put(Constants.PARAMS_USER_ID, String.valueOf(token.getUserId()));
        contextMap.put(Constants.PARAMS_CLIENT_ID, client.getClientId());
        contextMap.put(Constants.PARAMS_CLIENT_NAME, client.getClientName());
        contextMap.put(Constants.PARAMS_REQUEST_IP, event.getIp());
    }
}
