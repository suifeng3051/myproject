package com.zitech.gateway.apiconfig.service.imp;

import com.zitech.gateway.apiconfig.dao.gateway.IApiGroupDAO;
import com.zitech.gateway.apiconfig.dto.req.OpenResourceGroupReq;
import com.zitech.gateway.apiconfig.model.OpenApiGroup;
import com.zitech.gateway.apiconfig.service.IOpenApiGroupService;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

/**
 * Created by chenyun on 15/7/31.
 */
@Service
public class OpenApiGroupService implements IOpenApiGroupService {

    @Resource
    private IApiGroupDAO resourceGroupDAO;

    @Override
    public int insert(OpenApiGroup config) {
        resourceGroupDAO.save(config);
        return config.getId();
    }

    @Override
    public void update(OpenApiGroup config) {
        resourceGroupDAO.update(config);
    }

    @Override
    public void deleteById(int id) {
        resourceGroupDAO.deleteById(id);
    }

    @Override
    public OpenApiGroup getById(int id) {
        return resourceGroupDAO.getById(id);
    }

    @Override
    public List<OpenApiGroup> queryWithPage(OpenResourceGroupReq req) {
        return resourceGroupDAO.queryWithPage(req);
    }

    @Override
    public List<String> getGroupAlias() {
        return resourceGroupDAO.getGroupAlias();
    }

    @Override
    public List<OpenApiGroup> getAll() {
        return resourceGroupDAO.getAll();
    }
    @Override
    public List<OpenApiGroup> getGroupByNameAndAlias(String name, String alias){
        OpenApiGroup openApiGroup = new OpenApiGroup();
        openApiGroup.setName(name);
        openApiGroup.setAlias(alias);
        return resourceGroupDAO.getGroupByNameAndAlias(openApiGroup);
    }
}
