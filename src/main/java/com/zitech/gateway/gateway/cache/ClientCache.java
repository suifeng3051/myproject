package com.zitech.gateway.gateway.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.zitech.gateway.gateway.exception.CacheException;
import com.zitech.gateway.oauth.model.Client;
import com.zitech.gateway.oauth.service.ClientService;
import com.zitech.gateway.oauth.service.impl.OAuthServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
@LocalCache("client")
public class ClientCache implements ILocalCache {

    private static Logger logger = LoggerFactory.getLogger(ClientCache.class);

    @Autowired
    private OAuthServiceImpl oAuthService;

    @Autowired
    private ClientService clientsService;

    private Cache<String, Client> cache = CacheBuilder.newBuilder().maximumSize(10000).build();

    public Client get(String clientId) {
        try {
            return cache.get(clientId, () -> oAuthService.getClientByClientId(clientId));
        } catch (ExecutionException e) {
            logger.info("get cache error:", e);
            throw new CacheException(5214, "非法Client");
        }
    }

    @Override
    public void load() {
        List<String> clientIdList = clientsService.getClientIdList();
        for (String clientId : clientIdList) {
            get(clientId);
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
