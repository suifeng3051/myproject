package com.zitech.gateway.gateway.pipes.impl;

import com.zitech.gateway.apiconfig.model.Api;
import com.zitech.gateway.apiconfig.model.Group;
import com.zitech.gateway.gateway.cache.ClientCache;
import com.zitech.gateway.gateway.cache.GroupCache;
import com.zitech.gateway.gateway.exception.TokenValidateException;
import com.zitech.gateway.gateway.model.RequestEvent;
import com.zitech.gateway.oauth.model.AccessToken;
import com.zitech.gateway.oauth.model.Client;
import com.zitech.gateway.oauth.oauthex.OAuthConstants;
import com.zitech.gateway.utils.AppUtils;

import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
public class TokenPipe extends AbstractPipe {

    private static final Logger logger = LoggerFactory.getLogger(TokenPipe.class);

    @Autowired
    private ClientCache clientCache;

    @Autowired
    private GroupCache groupCache;

    @Override
    public void onEvent(RequestEvent event) {

        AccessToken accessToken = event.getAccessToken();

        if (!AppUtils.checkBefore(accessToken.getExpires())) {
            throw new TokenValidateException(OAuthConstants.OAuthResponse.NO_OR_EXPIRED_TOKEN,
                    OAuthConstants.OAuthDescription.INVALID_TOKEN_EXPIRED);
        }

        String clientId = accessToken.getClientId();
        Client client = clientCache.get(event.getId(), clientId);
        if (client == null) {
            throw new TokenValidateException(OAuthConstants.OAuthResponse.INVALID_CLIENT_NO,
                    OAuthConstants.OAuthDescription.INVALID_CLIENT_DESCRIPTION);
        }

        Api api = event.getApi();
        Group group = groupCache.get(event.getId(), api.getGroupId());

        Set<String> scopes = OAuthUtils.decodeScopes(accessToken.getScope());
        for (String scope : scopes) {
            if (scope.equals("all") || scope.equals(group.getAlias())) {
                return;
            }
        }

        // there may be some better ways, but...
        throw new TokenValidateException(OAuthConstants.OAuthResponse.INVALID_TOKEN,
                OAuthConstants.OAuthDescription.INVALID_RESOURCE);
    }

}
