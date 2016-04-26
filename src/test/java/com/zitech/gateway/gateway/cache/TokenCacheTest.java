package com.zitech.gateway.gateway.cache;

import com.alibaba.fastjson.JSON;
import com.zitech.gateway.AbstractJunit;
import com.zitech.gateway.cache.RedisOperate;
import com.zitech.gateway.gateway.exception.TokenValidateException;
import com.zitech.gateway.oauth.model.AccessToken;
import com.zitech.gateway.oauth.service.AccessTokenService;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by newlife on 16/3/24.
 */
public class TokenCacheTest extends AbstractJunit {

    @Autowired
    private AccessTokenCache accessTokensCache;

    @Autowired
    private AccessTokenService openOauthAccessTokensService;

    @Autowired
    private RedisOperate redis;

    @Test
    public void testAccessTokensCache() throws TokenValidateException {
        AccessToken token = accessTokensCache.get(null,"2de3d026d4a13c0da718302b1a664dbc");
        System.out.println(JSON.toJSONString(token));
    }

    @Test
    public void testGetAccessTokensCache() {
        List<AccessToken> list = openOauthAccessTokensService.getValidToken();
        System.out.println(JSON.toJSONString(list));
    }

    @Test
    public void testGetRedisCache() {
        System.out.println(JSON.toJSONString(redis.keys("open_oauth_access_token_*")));
    }

    @Test
    public void testDelRedisCache() {
        redis.delKeys("open_oauth_access_token_*");
        System.out.println(JSON.toJSONString(0));
    }

    @Test
    public void testSetRedisCache() {
        for(int i = 0; i < 20; i++) {
            redis.set("open_oauth_access_token_"+i,"open_oauth_access_token_"+i);
        }
    }

}
