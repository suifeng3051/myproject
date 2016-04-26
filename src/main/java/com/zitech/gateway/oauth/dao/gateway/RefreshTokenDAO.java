package com.zitech.gateway.oauth.dao.gateway;

import com.zitech.gateway.oauth.model.RefreshToken;

import java.util.List;

public interface RefreshTokenDAO {

    int save(RefreshToken refreshToken);

    RefreshToken getById(Integer id);

    RefreshToken getByToken(String token);

    List<RefreshToken> getByAccessTokens(List<String> list);

    void deleteByToken(String token);

    void deleteByAccessTokens(List<String> list);

    void deleteById(Integer id);

    void update(RefreshToken refreshToken);
}