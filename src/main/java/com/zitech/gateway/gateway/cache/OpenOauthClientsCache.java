package com.zitech.gateway.gateway.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.zitech.gateway.oauth.model.OpenOauthClients;
import com.zitech.gateway.oauth.service.IOpenOauthClientsService;
import com.zitech.gateway.oauth.service.impl.OAuthService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@LocalCache("client")
public class OpenOauthClientsCache implements ILocalCache {

    private static Logger logger = LoggerFactory.getLogger(OpenOauthClientsCache.class);

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private IOpenOauthClientsService clientsService;

    private Cache<String, OpenOauthClients> cache = CacheBuilder.newBuilder().maximumSize(10000).build();

    public OpenOauthClients get(UUID eventId, String clientId) {
        OpenOauthClients clients = null;
        try {
            clients = cache.get(clientId, () -> oAuthService.getClientByClientId(clientId));
        } catch (Exception e) {
            logger.error("error when getting cache: {}", eventId, e);
        }
        return clients;
    }

    @Override
    public void load() {
        List<String> clientIdList = clientsService.getClientIdList();
        for (String clientId : clientIdList) {
            get(null, clientId);
        }
    }

    public void clear() {
        cache.invalidateAll();
    }

    @Override
    public long cacheSize() {
        return cache.size();
    }
}
