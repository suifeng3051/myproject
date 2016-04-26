package com.zitech.gateway.apiconfig.dao.gateway;


import java.security.acl.Group;
import java.util.List;

public interface GroupDAO {

    List<String> selectAllGroupAlias();

    List<Group> selectAll();

    List<Group> selectGroupByNameAndAlias(Group openApiGroup);

    Group selectByAlias(String alias);

    int deleteByPrimaryKey(Long id);

    int insert(Group record);

    int insertSelective(Group record);

    Group selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Group record);

    int updateByPrimaryKey(Group record);
}
