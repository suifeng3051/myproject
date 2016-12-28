package com.zitech.gateway.apiconfig.service;


import com.zitech.gateway.apiconfig.model.Group;

import java.util.List;
import java.util.Map;


public interface GroupService {

    String prefix = "api_group_";

    void insert(Group config);

    void update(Group config);

    void deleteById(Integer id);

    Group getById(int id);

    List<String> getGroupAlias();

    List<Group> getAll();

    List<Group> getGroupByNameAndAlias(String name, String alias);

    Group getGroupByAlias(String alias);

    String getGroupNamesByIds(String ids);

    Map<String, Object> getGroupTreeById(int id);

    Group getTree();

    List<Group> getParents(Group root, int id);

    /**
     * 获取某一组的自身及其子组
     */
    List<Group> getAllById(int id);

    /**
     * 获取所有group的id与name的映射关系
     */
    Map<Integer, String> getAllNameIdMapping();


}
