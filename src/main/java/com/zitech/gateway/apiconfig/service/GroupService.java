package com.zitech.gateway.apiconfig.service;


import com.zitech.gateway.apiconfig.model.Group;

import java.util.List;
import java.util.Map;


public interface GroupService {

    String prefix = "api_group_";

    int insert(Group config);

    void update(Group config);

    void deleteById(Integer id);

    Group getById(int id);

    List<String> getGroupAlias();

    List<Group> getAll();

    List<Group> getGroupByNameAndAlias(String name, String alias);

    Group getGroupByAlias(String alias);

    String getGroupNamesByIds(String ids);

    Map<String, Object> getGroupTreeById(int id);

    }
