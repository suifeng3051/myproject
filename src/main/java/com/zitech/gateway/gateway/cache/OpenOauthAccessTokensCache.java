package com.zitech.gateway.gateway.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.zitech.gateway.gateway.exception.TokenValidateException;
import com.zitech.gateway.oauth.model.OpenOauthAccessTokens;
import com.zitech.gateway.oauth.oauthex.OAuthConstants;
import com.zitech.gateway.oauth.service.IOpenOauthAccessTokensService;
import com.zitech.gateway.oauth.service.impl.OAuthService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
@LocalCache("openOauthAccessTokens")
public class OpenOauthAccessTokensCache implements ILocalCache {

    private static Logger logger = LoggerFactory.getLogger(OpenOauthAccessTokensCache.class);

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private IOpenOauthAccessTokensService openOauthAccessTokensService;

    private Cache<String, OpenOauthAccessTokens> cache = CacheBuilder.newBuilder().maximumSize(100000).
            expireAfterWrite(30, TimeUnit.MINUTES).build();

    public OpenOauthAccessTokens get(UUID eventId, String accessToken) throws TokenValidateException {
        OpenOauthAccessTokens openOauthAccessTokens = null;
        try {
            openOauthAccessTokens = cache.get(accessToken, () -> oAuthService.getAccessToken(accessToken));
        } catch (Exception e) {
            throw new TokenValidateException(OAuthConstants.OAuthResponse.NO_OR_EXPIRED_TOKEN,
                    OAuthConstants.OAuthDescription.INVALID_TOKEN);
        }
        return openOauthAccessTokens;
    }

    @Override
    public void load() {

        /***access token need not load***/

        /*List<OpenOauthAccessTokens> list = openOauthAccessTokensService.getValidToken();
        if (null != list && list.size() > 0) {
            for (OpenOauthAccessTokens token : list) {
                get(null, token.getAccessToken());
            }
        }*/
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
