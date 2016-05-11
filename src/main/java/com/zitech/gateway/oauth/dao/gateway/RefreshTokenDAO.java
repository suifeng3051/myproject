package com.zitech.gateway.oauth.dao.gateway;

import com.zitech.gateway.oauth.model.RefreshToken;

import java.util.List;

public interface RefreshTokenDAO {

    int insert(RefreshToken refreshToken);

    RefreshToken selectByPrimaryKey(Integer id);

    RefreshToken selectByToken(String token);

    List<RefreshToken> selectByAccessTokens(List<String> list);

    void deleteByToken(String token);

    void deleteByAccessTokens(List<String> list);

    void deleteById(Integer id);

    void update(RefreshToken refreshToken);
}