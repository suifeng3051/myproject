package com.zitech.gateway.apiconfig.dao.gateway;


import com.zitech.gateway.apiconfig.dto.req.OpenResourceGroupReq;
import com.zitech.gateway.apiconfig.model.OpenApiGroup;
import com.zitech.platform.dao.base.func.IEntityDAO;

import java.util.List;

public interface IApiGroupDAO extends IEntityDAO<OpenApiGroup, OpenResourceGroupReq> {

    List<String> getGroupAlias();

    List<OpenApiGroup> getAll();

    List<OpenApiGroup> getGroupByNameAndAlias(OpenApiGroup openApiGroup);
}