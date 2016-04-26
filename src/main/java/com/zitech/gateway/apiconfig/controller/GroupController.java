package com.zitech.gateway.apiconfig.controller;

import com.alibaba.fastjson.JSON;
import com.zitech.gateway.apiconfig.service.GroupService;
import com.zitech.gateway.common.ApiResult;
import com.zitech.gateway.oauth.model.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
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
}
