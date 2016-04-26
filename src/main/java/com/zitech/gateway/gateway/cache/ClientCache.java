package com.zitech.gateway.gateway.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.zitech.gateway.oauth.model.Client;
import com.zitech.gateway.oauth.service.ClientService;
import com.zitech.gateway.oauth.service.impl.OAuthServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@LocalCache("client")
public class ClientCache implements ILocalCache {

    private static Logger logger = LoggerFactory.getLogger(ClientCache.class);

    @Autowired
    private OAuthServiceImpl oAuthService;

    @Autowired
    private ClientService clientsService;

    private Cache<String, Client> cache = CacheBuilder.newBuilder().maximumSize(10000).build();

    public Client get(UUID eventId, String clientId) {
        Client clients = null;
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
