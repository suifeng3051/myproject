package com.zitech.gateway.apiconfig.dao.gateway;


import com.zitech.gateway.apiconfig.model.Param;

import java.util.List;

public interface ParamDAO {
    int deleteByPrimaryKey(Integer id);

    int insert(Param record);

    int insertSelective(Param record);

    Param selectByPrimaryKey(Integer id);

    Param selectByApiId(Integer apiId);

    List<Param> selectAll();

    int updateByPrimaryKeySelective(Param record);

    int updateByPrimaryKey(Param record);
}