package com.zitech.gateway.oauth.dao.gateway;

import com.zitech.gateway.oauth.model.OpenOauthRefreshTokens;
import com.zitech.platform.dao.base.func.IEntityDAO;

import java.util.List;

public interface IOauthRefreshTokensDAO extends IEntityDAO<OpenOauthRefreshTokens, OpenOauthRefreshTokens> {

    OpenOauthRefreshTokens getByToken(String token);

    List<OpenOauthRefreshTokens> getByAccessTokens(List<String> list);

    void deleteByToken(String token);

    void deleteByAccessTokens(List<String> list);

}