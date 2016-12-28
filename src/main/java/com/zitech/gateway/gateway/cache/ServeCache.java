package com.zitech.gateway.gateway.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.zitech.gateway.AppConfig;
import com.zitech.gateway.apiconfig.model.Serve;
import com.zitech.gateway.apiconfig.service.ServeService;
import com.zitech.gateway.gateway.exception.CacheException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@LocalCache("serve")
public class ServeCache implements ILocalCache {

    private static Logger logger = LoggerFactory.getLogger(ServeCache.class);

    @Autowired
    private ServeService serveService;

    @Autowired
    private AppConfig appConfig;

    private Cache<String, Serve> cache = CacheBuilder.newBuilder().maximumSize(10000).build();

    public Serve get(Integer apiId) {
        try {
            return cache.get(String.valueOf(apiId), () -> {
                Serve serve = serveService.getByApiId(apiId);
                if (serve == null)
                    throw new CacheException(5214, "非法服务配置");
                return serve;
            });
        } catch (ExecutionException e) {
            logger.info("get cache error:", e);
            throw new CacheException(5217, "execution abort");
        }
    }

    @Override
    public void load() {
        List<Serve> serveList = serveService.getAll(appConfig.env);
        for (Serve serve : serveList) {
            get(serve.getApiId());
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
