package com.zitech.gateway.gateway.cache;

public interface ILocalCache {

    void load();

    void clear();

    long cacheSize();

}
