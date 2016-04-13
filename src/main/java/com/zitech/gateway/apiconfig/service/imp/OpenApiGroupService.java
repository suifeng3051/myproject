package com.zitech.gateway.apiconfig.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zitech.gateway.apiconfig.dao.gateway.IApiGroupDAO;
import com.zitech.gateway.apiconfig.dto.req.OpenResourceGroupReq;
import com.zitech.gateway.apiconfig.model.OpenApiGroup;
import com.zitech.gateway.apiconfig.service.IOpenApiGroupService;
import com.zitech.gateway.utils.AppUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public OpenApiGroup getGroupByAlias(String alias) {
        return  resourceGroupDAO.getByAlias(alias);
    }

    @Override
    public String getGroupNamesByIds(String ids){
        StringBuffer groupNames = new StringBuffer();

        if(StringUtils.isNotBlank(ids)){
            if(ids.trim().contains(" ")){
                String[] groupIds = ids.split(" ");
                for(String groupId:groupIds){
                    int id = Integer.parseInt(groupId.trim());
                    groupNames.append(resourceGroupDAO.getById(id).getAlias()+" ");
                }
            }else{
                    int id = Integer.parseInt(ids.trim());
                    groupNames.append(resourceGroupDAO.getById(id).getAlias()+" ");
            }
        }

        return groupNames.toString();

    }

    /**
     * add by pxl 2016.4.12 获取group的树形结构
     * @return
     */
    @Override
    public Map<String, Object> getGroupTreeById(int id) {

        Map<Integer,Map<String,Object>> groupTreeKeyByID = new HashMap<Integer,Map<String,Object>>();
        Map<Integer,List<Map<String,Object>>> groupTreeKeyByPID = new HashMap<Integer,List<Map<String,Object>>>();
        List<OpenApiGroup>  list_openApiGroup = resourceGroupDAO.getAll();

        //为了递归的时候不重新去数据库取
        for(OpenApiGroup openApiGroup:list_openApiGroup) {
            Map<String,Object>  treeObj = new HashMap<String,Object>();
            treeObj.put("pid",openApiGroup.getPid());
            treeObj.put("id",openApiGroup.getId());
            treeObj.put("name",openApiGroup.getName());
            treeObj.put("alias",openApiGroup.getAlias());
            treeObj.put("level",openApiGroup.getLevel());
            treeObj.put("children",new ArrayList<Map<String,Object>>());
            groupTreeKeyByID.put(openApiGroup.getId(), treeObj);

            List<Map<String,Object>> temp = new ArrayList<Map<String,Object>>();
            if (groupTreeKeyByPID.containsKey(openApiGroup.getPid())) {
                temp = groupTreeKeyByPID.get(openApiGroup.getPid());
            }
            temp.add(treeObj);
            groupTreeKeyByPID.put(openApiGroup.getPid(), temp);
        }


        Map<String,Object> treeObj = AppUtils.getTreeById(id,groupTreeKeyByID,groupTreeKeyByPID);

        return treeObj;
    }




}
