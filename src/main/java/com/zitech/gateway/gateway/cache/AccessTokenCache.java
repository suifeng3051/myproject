package com.zitech.gateway.gateway.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.zitech.gateway.gateway.exception.TokenValidateException;
import com.zitech.gateway.oauth.model.AccessToken;
import com.zitech.gateway.oauth.oauthex.OAuthConstants;
import com.zitech.gateway.oauth.service.AccessTokenService;
import com.zitech.gateway.oauth.service.impl.OAuthServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


@Service
@LocalCache("access_token")
public class AccessTokenCache implements ILocalCache {

    private static Logger logger = LoggerFactory.getLogger(AccessTokenCache.class);

    @Autowired
    private OAuthServiceImpl oAuthService;

    private Cache<String, AccessToken> cache = CacheBuilder.newBuilder().maximumSize(100000).
            expireAfterWrite(30, TimeUnit.MINUTES).build();

    public AccessToken get(String accessToken) throws Exception {
        return cache.get(accessToken, () -> oAuthService.getAccessToken(accessToken));
    }

    @Override
    public void load() {
        //nothing
    }

    @Override
    public void clear() {
        cache.invalidateAll();
    }

    @Override
    public long cacheSize() {
        return cache.size();
    }
}
