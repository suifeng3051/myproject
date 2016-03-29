package com.zitech.gateway.apiconfig.service;


import com.zitech.gateway.apiconfig.dto.req.OpenResourceGroupReq;
import com.zitech.gateway.apiconfig.model.OpenResourceGroup;

import java.util.List;

/**
 * Created by chenyun on 15/7/31.
 */
public interface IOpenResourceGroupService {

    final String prefix = "open_resource_group_";

    int insert(OpenResourceGroup config);

    void update(OpenResourceGroup config);

    void deleteById(int id);

    OpenResourceGroup getById(int id);

    List<OpenResourceGroup> queryWithPage(OpenResourceGroupReq req);

    List<String> getGroupAlias();

    List<OpenResourceGroup> getAll();

    List<OpenResourceGroup> getGroupByNameAndAlias(String name, String alias);

}
