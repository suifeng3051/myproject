package com.zitech.gateway.gateway.services;

import com.zitech.gateway.AppConfig;
import com.zitech.gateway.apiconfig.model.CarmenApi;
import com.zitech.gateway.gateway.cache.CarmenApiCache;
import com.zitech.gateway.gateway.cache.AccessTokenCache;
import com.zitech.gateway.gateway.cache.ClientCache;
import com.zitech.gateway.gateway.exception.TokenValidateException;
import com.zitech.gateway.gateway.model.RequestEvent;
import com.zitech.gateway.oauth.model.AccessToken;
import com.zitech.gateway.oauth.model.Client;
import com.zitech.gateway.oauth.oauthex.OAuthConstants;
import com.zitech.gateway.oauth.service.impl.BaseService;
import com.zitech.gateway.utils.AppUtils;

import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.concurrent.ExecutionException;

@Service
public class TokenService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    @Autowired
    private ClientCache clientsCache;

    @Autowired
    private AccessTokenCache accessTokensCache;

    @Autowired
    private CarmenApiCache carmenApiCache;

    @Autowired
    private AppConfig appConfig;

    /**
     * validate access token, please see http://gateway.zitech.com/doc/api/oauthprotocol
     */
    public AccessToken validateAccessToken(RequestEvent event) throws TokenValidateException, ExecutionException {

        String accessToken = event.getAccessToken();

        if (StringUtils.isEmpty(accessToken)) {
            throw new TokenValidateException(OAuthConstants.OAuthResponse.NO_TOKEN,
                    OAuthConstants.OAuthDescription.INVALID_TOKEN);
        }

        if (StringUtils.isEmpty(event.getNamespace()) ||
                StringUtils.isEmpty(event.getMethod()) ||
                StringUtils.isEmpty(event.getVersion())) {
            throw new TokenValidateException(OAuthConstants.OAuthResponse.INVALID_API_STRUCTURE_CODE,
                    OAuthConstants.OAuthDescription.INVALID_API_STRUCTURE);
        }

        /*OpenOauthAccessTokens openOauthAccessTokens;
        try {
            event.getTicTac().tic(Constants.ST_REDIS_TOKEN);
            openOauthAccessTokens = accessTokensCache.get(event.getId(), accessToken);
        } finally {
            event.getTicTac().tac(Constants.ST_REDIS_TOKEN);
        }
        if (openOauthAccessTokens == null) {
            throw new TokenValidateException(OAuthConstants.OAuthResponse.NO_OR_EXPIRED_TOKEN,
                    OAuthConstants.OAuthDescription.INVALID_TOKEN);
        }*/
        AccessToken openOauthAccessTokens = accessTokensCache.get(event.getId(), accessToken);

        /**
         * check if expired
         */
        if (!AppUtils.checkBefore(openOauthAccessTokens.getExpires())) {
            throw new TokenValidateException(OAuthConstants.OAuthResponse.NO_OR_EXPIRED_TOKEN,
                    OAuthConstants.OAuthDescription.INVALID_TOKEN_EXPIRED);
        }

        String clientId = openOauthAccessTokens.getClientId();
        Client openOauthClients = clientsCache.get(event.getId(), clientId);
        if (openOauthClients == null) {
            throw new TokenValidateException(OAuthConstants.OAuthResponse.INVALID_CLIENT_NO,
                    OAuthConstants.OAuthDescription.INVALID_CLIENT_DESCRIPTION);
        }

        /**
         * check api resource
         */
        CarmenApi carmenApi = carmenApiCache.get(event.getId(), event.getNamespace(), event.getMethod(),
                event.getVersion(), appConfig.env);
        //OpenResource openResource = resourceCache.get(event.getId(), resource);
        if (carmenApi == null) {
            throw new TokenValidateException(OAuthConstants.OAuthResponse.NO_API,
                    OAuthConstants.OAuthDescription.INVALID_API);
        }

        /**
         * check scope info, if it has 'all' in the token then return immediately
         */
        Set<String> scopes = OAuthUtils.decodeScopes(openOauthAccessTokens.getScope());
        for (String scope : scopes) {
            if (scope.equals("all") || scope.equals(carmenApi.getApiGroup())) {
                return openOauthAccessTokens;
            }
        }

        // there may be some better ways, but...
        throw new TokenValidateException(OAuthConstants.OAuthResponse.INVALID_TOKEN,
                OAuthConstants.OAuthDescription.INVALID_RESOURCE);
    }

}