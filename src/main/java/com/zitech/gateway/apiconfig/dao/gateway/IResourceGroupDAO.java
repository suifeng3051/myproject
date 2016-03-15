package com.zitech.gateway.apiconfig.dao.gateway;


import com.zitech.gateway.apiconfig.dto.req.OpenResourceGroupReq;
import com.zitech.gateway.apiconfig.model.OpenResourceGroup;
import com.zitech.platform.dao.base.func.IEntityDAO;

import java.util.List;

public interface IResourceGroupDAO extends IEntityDAO<OpenResourceGroup, OpenResourceGroupReq> {

    List<String> getGroupAlias();

    List<OpenResourceGroup> getAll();

}