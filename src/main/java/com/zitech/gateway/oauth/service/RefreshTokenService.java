package com.zitech.gateway.oauth.service;


import com.zitech.gateway.oauth.model.RefreshToken;

import java.util.List;

public interface RefreshTokenService {

    RefreshToken getById(Integer id);

    RefreshToken getByToken(String token);

    List<RefreshToken> getByAccessTokens(List<String> list);

    void deleteById(Integer id);

    void deleteByToken(String token);

    void deleteByAccessTokens(List<String> list);

    int save(RefreshToken openOauthRefreshTokens);

    void update(RefreshToken openOauthRefreshTokens);

    void saveRefreshTokenToMongoDb(List<String> tkList);
}
