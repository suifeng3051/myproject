package com.zitech.gateway.oauth.dao.gateway;


import com.zitech.gateway.oauth.model.AccessToken;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AccessTokenDAO {

    Integer insert(AccessToken accessToken);

    AccessToken selectByPrimaryKey(Integer token);

    AccessToken selectByToken(String token);

    List<AccessToken> selectValidToken();

    List<AccessToken> selectByClientIdAndUserId(@Param("clientId") String clientId, @Param("userId") int userId);

    void deleteByToken(String token);

    void deleteByIds(List<Integer> list);

    void deleteById(Integer id);

    void update(AccessToken accessToken);
}