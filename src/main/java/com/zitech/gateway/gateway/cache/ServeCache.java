package com.zitech.gateway.gateway.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.zitech.gateway.AppConfig;
import com.zitech.gateway.apiconfig.model.Api;
import com.zitech.gateway.apiconfig.model.Serve;
import com.zitech.gateway.apiconfig.service.ServeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@LocalCache("serve")
public class ServeCache implements ILocalCache {

    private static Logger logger = LoggerFactory.getLogger(ServeCache.class);

    @Autowired
    private ServeService serveService;

    @Autowired
    private AppConfig appConfig;

    private Cache<String, Serve> cache = CacheBuilder.newBuilder().maximumSize(10000).build();

    public Serve get(UUID eventId, Integer apiId) {
        Serve serve = null;
        try {
            serve = cache.get(String.valueOf(apiId), () -> serveService.getByApiId(apiId));
        } catch (Exception e) {
            logger.error("error when getting cache: {}", eventId, e);
        }
        return serve;
    }

    @Override
    public void load() {
        List<Serve> serveList = serveService.getAll();
        for (Serve serve : serveList) {
            get(null, serve.getApiId());
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
