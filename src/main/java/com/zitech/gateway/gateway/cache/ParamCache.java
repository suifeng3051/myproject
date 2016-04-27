package com.zitech.gateway.gateway.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.zitech.gateway.AppConfig;
import com.zitech.gateway.apiconfig.service.ParamService;
import com.zitech.gateway.common.ParamException;
import com.zitech.gateway.param.Param;
import com.zitech.gateway.param.ParamHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@LocalCache("param")
public class ParamCache implements ILocalCache {

    private static Logger logger = LoggerFactory.getLogger(ParamCache.class);

    @Autowired
    private ParamService paramService;

    @Autowired
    private AppConfig appConfig;

    private Cache<String, Param> cache = CacheBuilder.newBuilder().maximumSize(10000).build();

    public Param get(Integer apiId) throws Exception {
        return cache.get(String.valueOf(apiId), () -> {
            com.zitech.gateway.apiconfig.model.Param p = paramService.getByApiId(apiId);
            if(p == null)
                throw new ParamException(5209, "no param struct");

            return ParamHelper.buildTree(p.getRequestStructure());
        });
    }

    @Override
    public void load() throws Exception {
        List<com.zitech.gateway.apiconfig.model.Param> paramList = paramService.getAll();
        for (com.zitech.gateway.apiconfig.model.Param param : paramList) {
            this.get(param.getApiId());
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
