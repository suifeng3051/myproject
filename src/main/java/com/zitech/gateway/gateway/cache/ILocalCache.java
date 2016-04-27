package com.zitech.gateway.gateway.cache;

public interface ILocalCache {

    void load() throws Exception;

    void clear();

    long cacheSize();

}
