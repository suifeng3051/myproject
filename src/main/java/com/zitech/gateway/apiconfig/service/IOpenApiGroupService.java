package com.zitech.gateway.apiconfig.service;


import com.zitech.gateway.apiconfig.dto.req.OpenResourceGroupReq;
import com.zitech.gateway.apiconfig.model.OpenApiGroup;

import java.util.List;

/**
 * Created by chenyun on 15/7/31.
 */
public interface IOpenApiGroupService {

    final String prefix = "open_resource_group_";

    int insert(OpenApiGroup config);

    void update(OpenApiGroup config);

    void deleteById(int id);

    OpenApiGroup getById(int id);

    List<OpenApiGroup> queryWithPage(OpenResourceGroupReq req);

    List<String> getGroupAlias();

    List<OpenApiGroup> getAll();

    List<OpenApiGroup> getGroupByNameAndAlias(String name, String alias);

}
