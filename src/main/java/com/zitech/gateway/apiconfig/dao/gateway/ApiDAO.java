package com.zitech.gateway.apiconfig.dao.gateway;


import com.zitech.gateway.apiconfig.model.Api;

import java.util.List;

public interface ApiDAO {
    int deleteByPrimaryKey(Integer id);

    int insert(Api record);

    int insertSelective(Api record);

    Api selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Api record);

    int updateByPrimaryKeyWithBLOBs(Api record);

    int updateByPrimaryKey(Api record);

    List<Api> getAllApi();

    List<Api> getByGroupId(Integer groupId);
}