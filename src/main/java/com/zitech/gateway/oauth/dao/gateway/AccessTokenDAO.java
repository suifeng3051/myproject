package com.zitech.gateway.oauth.dao.gateway;


import com.zitech.gateway.oauth.model.AccessToken;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AccessTokenDAO {

    Integer save(AccessToken accessToken);

    AccessToken getById(Integer token);

    AccessToken getByToken(String token);

    List<AccessToken> getValidToken();

    List<AccessToken> getByClientIdAndUserId(@Param("clientId") String clientId, @Param("userId") int userId);

    void deleteByToken(String token);

    void deleteByIds(List<Integer> list);

    void deleteById(Integer id);

    void update(AccessToken accessToken);
}