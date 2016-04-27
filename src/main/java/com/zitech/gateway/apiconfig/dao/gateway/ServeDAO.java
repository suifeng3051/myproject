package com.zitech.gateway.apiconfig.dao.gateway;


import com.zitech.gateway.apiconfig.model.Serve;

import java.util.List;

public interface ServeDAO {
    int deleteByPrimaryKey(Integer id);

    int insert(Serve record);

    int insertSelective(Serve record);

    Serve selectByPrimaryKey(Integer id);

    Serve selectByApiId(Integer apiId);

    List<Serve> selectAll(Byte env);

    int updateByPrimaryKeySelective(Serve record);

    int updateByPrimaryKey(Serve record);
}