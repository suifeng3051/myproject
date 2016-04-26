package com.zitech.gateway.apiconfig.service.imp;

import com.zitech.gateway.apiconfig.dao.gateway.GroupDAO;
import com.zitech.gateway.apiconfig.dto.req.OpenResourceGroupReq;
import com.zitech.gateway.apiconfig.model.Group;
import com.zitech.gateway.apiconfig.service.GroupService;
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
public class GroupServiceImpl implements GroupService {

    @Resource
    private GroupDAO resourceGroupDAO;

    @Override
    public int insert(Group config) {
        resourceGroupDAO.save(config);
        return config.getId();
    }

    @Override
    public void update(Group config) {
        resourceGroupDAO.update(config);
    }

    @Override
    public void deleteById(int id) {
        resourceGroupDAO.deleteById(id);
    }

    @Override
    public Group getById(int id) {
        return resourceGroupDAO.getById(id);
    }

    @Override
    public List<Group> queryWithPage(OpenResourceGroupReq req) {
        return resourceGroupDAO.queryWithPage(req);
    }

    @Override
    public List<String> getGroupAlias() {
        return resourceGroupDAO.selectAllGroupAlias();
    }

    @Override
    public List<Group> getAll() {
        return resourceGroupDAO.selectAll();
    }
    @Override
    public List<Group> getGroupByNameAndAlias(String name, String alias){
        Group openApiGroup = new Group();
        openApiGroup.setName(name);
        openApiGroup.setAlias(alias);
        return resourceGroupDAO.selectGroupByNameAndAlias(openApiGroup);
    }

    @Override
    public Group getGroupByAlias(String alias) {
        return  resourceGroupDAO.selectByAlias(alias);
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

        if(id==-1){
            Group openApiGroup_all = resourceGroupDAO.selectByAlias("all");
            id=openApiGroup_all.getId();
        }


        Map<Integer,Map<String,Object>> groupTreeKeyByID = new HashMap<Integer,Map<String,Object>>();
        Map<Integer,List<Map<String,Object>>> groupTreeKeyByPID = new HashMap<Integer,List<Map<String,Object>>>();
        List<Group>  list_openApiGroup = resourceGroupDAO.selectAll();

        //为了递归的时候不重新去数据库取
        for(Group openApiGroup:list_openApiGroup) {

            if(StringUtils.isEmpty(openApiGroup.getAlias()) || StringUtils.isEmpty(openApiGroup.getName())) {
                continue;
            }

            Map<String,Object>  treeObj = new HashMap<String,Object>();
            treeObj.put("pid",openApiGroup.getPid());
            treeObj.put("id",openApiGroup.getId());
            treeObj.put("name",openApiGroup.getName());
            treeObj.put("alias",openApiGroup.getAlias());
            treeObj.put("level",openApiGroup.getLevel());
            treeObj.put("children",null);
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
