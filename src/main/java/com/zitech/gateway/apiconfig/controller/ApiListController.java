package com.zitech.gateway.apiconfig.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zitech.gateway.apiconfig.model.Api;
import com.zitech.gateway.apiconfig.model.Group;
import com.zitech.gateway.apiconfig.service.AdminService;
import com.zitech.gateway.apiconfig.service.ApiService;
import com.zitech.gateway.apiconfig.service.GroupService;
import com.zitech.gateway.cache.RedisOperate;
import com.zitech.gateway.common.ApiResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.*;

/**
 * Created by hy on 16-4-26.
 */
@Controller
public class ApiListController {
    private final static Logger logger = LoggerFactory.getLogger(ApiListController.class);

    @Autowired
    private RedisOperate redisOperate;

    @Autowired
    private AdminService adminService;

    @Autowired
    private ApiService apiService;

    @Autowired
    private GroupService groupService;



    @RequestMapping("/apilist")
    public ModelAndView getApiList(@RequestParam(value = "env",required = false, defaultValue = "1") Byte env,
            HttpServletRequest request, HttpServletResponse response)
    {
        Map<String, Object> results = new HashMap<>();
        try {
            String userName = adminService.getUserNameFromSessionAndRedis(request);

            if (userName == null) {
                return new ModelAndView("redirect:/unifyerror", "cause", "Fail to get user name");
            }

            //api
            List<Api> allApi = apiService.getAllByEnv(env);
            if(null != allApi) {
                Collections.sort(allApi, new Comparator<Api>() {
                    @Override
                    public int compare(Api arg1, Api arg2) {
                        if(org.springframework.util.StringUtils.isEmpty(arg1.getCreatedTime()) ||
                                org.springframework.util.StringUtils.isEmpty(arg2.getCreatedTime())){ // 防止脏数据
                            return 0;
                        }
                        return arg2.getCreatedTime().compareTo(arg1.getCreatedTime()); // 按时间逆序排序
                    }
                });
            }
            //group
            Map<String, Object> groupMap = groupService.getGroupTreeById(-1);

            results.put("user", userName);
            results.put("isAdmin", adminService.isAdmin(userName));
            results.put("apilists", allApi);
            results.put("groupMap", groupMap);
            results.put("env", env);
        } catch (Exception e) {
            logger.error("get api list error",e);
            return new ModelAndView("redirect:/unifyerror", "cause", "error when get api list");
        }

        return new ModelAndView("apilist", "results", results);
    }

    @RequestMapping(value = "/getapi/by/groupid", produces="application/json;charset=utf-8")
    @ResponseBody
    public String getApiByGroup(@RequestParam("groupid") Integer groupid,
                                @RequestParam(value = "env",required = false, defaultValue = "1") Byte env)
    {
        ApiResult<List<Api>> apiResult = new ApiResult<>(0,"success");

        try {
            List<Api> list = null;
            if (groupid == null || groupid == 1) {
                list = apiService.getAllByEnv(env);
            } else {
                list = apiService.getByGroupIdAndEnv(groupid,env);
            }
            Collections.sort(list, new Comparator<Api>() {
                @Override
                public int compare(Api arg1, Api arg2) {
                    if(org.springframework.util.StringUtils.isEmpty(arg1.getCreatedTime()) || org.springframework.util.StringUtils.isEmpty(arg2.getCreatedTime())){ // 防止脏数据
                        return 0;
                    }
                    return arg2.getCreatedTime().compareTo(arg1.getCreatedTime()); // 按时间逆序排序
                }
            });
            apiResult.setData(list);
        } catch (Exception e) {
            logger.info("根据groupid获取api发生异常",e);
            apiResult.setCode(9000);
            apiResult.setMessage("根据grupid获取api发生异常");
        }
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
