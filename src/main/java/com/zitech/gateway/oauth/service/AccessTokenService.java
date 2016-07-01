package com.zitech.gateway.oauth.service;


import com.zitech.gateway.oauth.model.AccessToken;

import java.util.List;

public interface AccessTokenService {

    AccessToken getById(Integer id);

    AccessToken getByToken(String token);

    List<AccessToken> getValidToken();

    List<AccessToken> getByClientIdAndUserId(String clientId, int adminId);

    void deleteById(Integer id);

    void deleteByIds(List<Integer> list);

    void deleteByToken(String token);

    int save(AccessToken openOauthAccessTokens);

    void update(AccessToken openOauthAccessTokens);


    void saveAccessTokenToMongoDb(List<AccessToken> tokensList);
}
