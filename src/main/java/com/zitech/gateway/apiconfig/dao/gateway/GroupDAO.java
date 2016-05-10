package com.zitech.gateway.apiconfig.dao.gateway;


import com.zitech.gateway.apiconfig.model.Group;

import java.util.List;

public interface GroupDAO {

    List<String> selectAllGroupAlias();

    List<Group> selectAll();

    List<Group> selectGroupByNameAndAlias(Group openApiGroup);

    Group selectByAlias(String alias);

    int deleteByPrimaryKey(Integer id);

    int insert(Group record);

    int insertSelective(Group record);

    Group selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Group record);

    int updateByPrimaryKey(Group record);


    /**
     * 获取自身及其所有下级的对象
     */
    List<Group> selectAllById(Integer id);

}
