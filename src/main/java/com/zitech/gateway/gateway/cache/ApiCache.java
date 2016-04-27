package com.zitech.gateway.gateway.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.zitech.gateway.AppConfig;
import com.zitech.gateway.apiconfig.model.Api;
import com.zitech.gateway.apiconfig.service.ApiService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@LocalCache("api")
public class ApiCache implements ILocalCache {

    private static Logger logger = LoggerFactory.getLogger(ApiCache.class);

    @Autowired
    private ApiService apiService;

    @Autowired
    private AppConfig appConfig;

    private Cache<String, Api> cache = CacheBuilder.newBuilder().maximumSize(10000).build();

    public Api get(UUID eventId, String namespace, String method, Integer version) {
        Api api = null;
        try {
            String id = String.format("%s-%s-%s-%s", namespace, method, version, appConfig.env);
            api = cache.get(id, () -> apiService.getApi(namespace, method, version, appConfig.env));
        } catch (Exception e) {
            logger.error("error when getting cache: {}", eventId, e);
        }
        return api;
    }

    @Override
    public void load() {
        List<Api> apiList = apiService.getAllByEnv(appConfig.env);
        for (Api api : apiList) {
            this.get(null, api.getNamespace(), api.getMethod(), api.getVersion());
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
