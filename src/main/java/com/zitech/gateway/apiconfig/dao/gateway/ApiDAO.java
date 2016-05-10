package com.zitech.gateway.apiconfig.dao.gateway;


import com.zitech.gateway.apiconfig.model.Api;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ApiDAO {
    int deleteByPrimaryKey(Integer id);

    int insert(Api record);

    int insertSelective(Api record);

    Api selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Api record);

    int updateByPrimaryKeyWithBLOBs(Api record);

    int updateByPrimaryKey(Api record);

    Api findApiForCheck(Map<String, Object> paraMap);

    List<Api> getAllApiByEnv(@Param("env") Byte env);

    List<Api> getByGroupIdAndEnv(@Param("groupId") Integer groupId, @Param("env") Byte env);

    List<Api> getDeletedApi();

    int getMaxId();

//    void saveResult(Map paraMap);


}