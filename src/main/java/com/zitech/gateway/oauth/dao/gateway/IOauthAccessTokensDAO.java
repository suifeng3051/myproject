package com.zitech.gateway.oauth.dao.gateway;


import com.zitech.gateway.oauth.model.OpenOauthAccessTokens;
import com.zitech.platform.dao.base.func.IEntityDAO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IOauthAccessTokensDAO extends IEntityDAO<OpenOauthAccessTokens, OpenOauthAccessTokens> {

    OpenOauthAccessTokens getByToken(String token);

    List<OpenOauthAccessTokens> getByClientIdAndUserId(@Param("clientId") String clientId, @Param("userId") int userId);

    void deleteByToken(String token);

    void deleteByIds(List<Integer> list);

}