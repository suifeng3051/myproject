package com.zitech.gateway.apiconfig.service.imp;

import com.zitech.gateway.apiconfig.dao.gateway.GroupDAO;
import com.zitech.gateway.apiconfig.model.Group;
import com.zitech.gateway.apiconfig.service.GroupService;
import com.zitech.gateway.utils.AppUtils;
import com.zitech.gateway.utils.TreeHelper;
import com.zitech.gateway.utils.TreeNode;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class GroupServiceImpl implements GroupService {

    @Resource
    private GroupDAO groupDAO;

    @Override
    public void insert(Group config) {
        groupDAO.insert(config);
    }

    @Override
    public void update(Group config) {
        groupDAO.updateByPrimaryKey(config);
    }

    @Override
    public void deleteById(Integer id) {
        groupDAO.deleteByPrimaryKey(id);
    }

    @Override
    public Group getById(int id) {
        return groupDAO.selectByPrimaryKey(id);
    }

    @Override
    public List<String> getGroupAlias() {
        return groupDAO.selectAllGroupAlias();
    }

    @Override
    public List<Group> getAll() {
        return groupDAO.selectAll();
    }

    @Override
    public List<Group> getGroupByNameAndAlias(String name, String alias) {
        Group openApiGroup = new Group();
        openApiGroup.setName(name);
        openApiGroup.setAlias(alias);
        return groupDAO.selectGroupByNameAndAlias(openApiGroup);
    }

    @Override
    public Group getGroupByAlias(String alias) {
        return groupDAO.selectByAlias(alias);
    }

    @Override
    public String getGroupNamesByIds(String ids) {
        StringBuffer groupNames = new StringBuffer();

        if (StringUtils.isNotBlank(ids)) {
            if (ids.trim().contains(" ")) {
                String[] groupIds = ids.split(" ");
                for (String groupId : groupIds) {
                    int id = Integer.parseInt(groupId.trim());
                    groupNames.append(groupDAO.selectByPrimaryKey(id).getAlias() + " ");
                }
            } else {
                int id = Integer.parseInt(ids.trim());
                groupNames.append(groupDAO.selectByPrimaryKey(id).getAlias() + " ");
            }
        }

        return groupNames.toString();

    }

    /**
     * add by pxl 2016.4.12 获取group的树形结构
     */
    @Override
    public Map<String, Object> getGroupTreeById(int id) {

        if (id == -1) {
            Group openApiGroup_all = groupDAO.selectByAlias("all");
            if (openApiGroup_all == null) {
                return new HashMap<>();
            }
            id = openApiGroup_all.getId();
        }


        Map<Integer, Map<String, Object>> groupTreeKeyByID = new HashMap<>();
        Map<Integer, List<Map<String, Object>>> groupTreeKeyByPID = new HashMap<>();
        List<Group> list_openApiGroup = groupDAO.selectAll();

        //为了递归的时候不重新去数据库取
        for (Group openApiGroup : list_openApiGroup) {

            if (StringUtils.isEmpty(openApiGroup.getAlias()) || StringUtils.isEmpty(openApiGroup.getName())) {
                continue;
            }

            Map<String, Object> treeObj = new HashMap<String, Object>();
            treeObj.put("pid", openApiGroup.getPid());
            treeObj.put("id", openApiGroup.getId());
            treeObj.put("name", openApiGroup.getName());
            treeObj.put("alias", openApiGroup.getAlias());
            treeObj.put("level", openApiGroup.getLevel());
            treeObj.put("children", null);
            groupTreeKeyByID.put(openApiGroup.getId(), treeObj);

            List<Map<String, Object>> temp = new ArrayList<Map<String, Object>>();
            if (groupTreeKeyByPID.containsKey(openApiGroup.getPid())) {
                temp = groupTreeKeyByPID.get(openApiGroup.getPid());
            }
            temp.add(treeObj);
            groupTreeKeyByPID.put(openApiGroup.getPid(), temp);
        }


        Map<String, Object> treeObj = AppUtils.getTreeById(id, groupTreeKeyByID, groupTreeKeyByPID);

        return treeObj;
    }

    @Override
    public Group getTree() {
        List<Group> all = this.getAll();
        TreeNode root = TreeHelper.buildTree(all);
        return (Group) root;
    }

    @Override
    public List<Group> getParents(Group root, int id) {
        List<TreeNode> parents = TreeHelper.getParentNodes(root, id);
        List<Group> rts = new ArrayList<>();
        parents.stream().forEach(node -> rts.add((Group) node));
        return rts;
    }

    @Override
    public List<Group> getAllById(int id) {

        return groupDAO.selectAllById(id);

    }

    @Override
    public Map<Integer, String> getAllNameIdMapping() {

        Map<Integer, String> map = new HashMap<>();
        List<Group> list = getAll();

        for (Group group : list) {
            map.put(group.getId(), group.getName());
        }

        return map;
    }


}
