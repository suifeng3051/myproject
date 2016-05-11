package com.zitech.gateway.gateway.pipes.impl;

import com.zitech.gateway.apiconfig.model.Api;
import com.zitech.gateway.apiconfig.model.Group;
import com.zitech.gateway.gateway.cache.GroupCache;
import com.zitech.gateway.gateway.exception.TokenException;
import com.zitech.gateway.gateway.model.RequestEvent;
import com.zitech.gateway.oauth.model.AccessToken;
import com.zitech.gateway.oauth.oauthex.OAuthConstants;
import com.zitech.gateway.utils.AppUtils;

import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
@Pipe(Group = 'A', Order = 3)
public class TokenPipe extends AbstractPipe {

    private static final Logger logger = LoggerFactory.getLogger(TokenPipe.class);

    @Autowired
    private GroupCache groupCache;

    @Override
    public void onEvent(RequestEvent event) throws Exception {

        AccessToken accessToken = event.getAccessToken();

        if (!AppUtils.checkBefore(accessToken.getExpires())) {
            throw new TokenException(OAuthConstants.OAuthResponse.NO_OR_EXPIRED_TOKEN,
                    OAuthConstants.OAuthDescription.INVALID_TOKEN_EXPIRED);
        }

        Api api = event.getApi();
        Group group = groupCache.get(api.getGroupId());

        Set<String> scopes = OAuthUtils.decodeScopes(accessToken.getScope());
        for (String scope : scopes) {
            if (scope.equals("all") || scope.equals(group.getAlias())) {
                return;
            }
        }

        // there may be some better ways, but...
        throw new TokenException(OAuthConstants.OAuthResponse.INVALID_TOKEN,
                OAuthConstants.OAuthDescription.INVALID_RESOURCE);
    }
}
