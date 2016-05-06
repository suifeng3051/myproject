package com.zitech.gateway.apiconfig.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zitech.gateway.apiconfig.model.Group;
import com.zitech.gateway.apiconfig.service.GroupService;
import com.zitech.gateway.common.ApiResult;
import com.zitech.gateway.oauth.model.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hy on 16-4-26.
 */
@Controller
public class GroupController {
    private final static Logger logger = LoggerFactory.getLogger(ClientController.class);

    @Autowired
    private GroupService groupService;

    @RequestMapping(value="/getGroupTree",produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getGroupTree()
    {
        ApiResult<Map<String,Object>> apiResult = new ApiResult<>(0, "success");

        Map<String,Object> groupTree = new HashMap<>();
        try{
            groupTree = groupService.getGroupTreeById(-1);
        }catch(Exception e)
        {
            logger.info("获取Group失败",e);
            apiResult.setCode(9010);
            apiResult.setMessage("获取Group失败");
        }
        apiResult.setData(groupTree);

        return apiResult.toString();
    }


    /**
     *
     * 在open_resource_group表中插入数据
     * @param insert 待插入
     * @return 插入成功返回success，插入失败返回fail
     */
    @RequestMapping(value = "/insertresourcegroup", produces="application/json;charset=utf-8")
    @ResponseBody
    public String insertResourceGroup(@RequestParam("insert") String insert) {
        String message = "fail";
        int code = 0;

        try {
            insert = URLDecoder.decode(insert, "UTF-8");
            JSONObject obj  = JSON.parseObject(insert);
            Group groupMoel = new Group();
            groupMoel.setName(obj.getString("name"));
            groupMoel.setAlias(obj.getString("alias"));
            groupMoel.setLevel(obj.getInteger("level"));
            groupMoel.setPid(obj.getInteger("parentId"));

            List<Group> groupByNameAndAlias = groupService.getGroupByNameAndAlias(groupMoel.getName(),groupMoel.getAlias());

            if(groupByNameAndAlias==null ||groupByNameAndAlias.size()!=0)
            {
                return new ApiResult<>(1,"组已经存在！").toString();
            }



            groupMoel.setCreateTime(new Date());
            groupService.insert(groupMoel);
            message = "success";
        } catch (Exception e) {
            code =1;
            message = message + e.toString();
            logger.error("fail to insert resource group.", e);
        }


        return new ApiResult<>(code,message).toString();

    }


}
