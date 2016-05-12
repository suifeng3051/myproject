package com.zitech.gateway.apiconfig.controller;

import com.alibaba.fastjson.JSONObject;
import com.zitech.gateway.AppConfig;
import com.zitech.gateway.apiconfig.model.Api;
import com.zitech.gateway.apiconfig.service.AdminService;
import com.zitech.gateway.apiconfig.service.ApiService;
import com.zitech.gateway.apiconfig.service.GroupService;
import com.zitech.gateway.cache.RedisOperate;
import com.zitech.gateway.common.ApiResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    @Autowired
    private AppConfig appConfig;


    @RequestMapping("/apilist")
    public ModelAndView getApiList(@RequestParam(value = "envToSet", required = false, defaultValue = "") Byte envToSet,
                                   HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> results = new HashMap<>();
        try {
            String userName = adminService.getUserNameFromSessionAndRedis(request);
            boolean isAdmin = adminService.isAdmin(userName);

            if (userName == null) {
                return new ModelAndView("redirect:/unifyerror", "cause", "Fail to get user name");
            }

            byte env = appConfig.env;
            String groupId_show = "";

            Cookie[] cookies = request.getCookies();
            if (cookies != null && cookies.length > 0) { //如果没有设置过Cookie会返回null
                for (Cookie cookie : cookies) {
                    String cookieName = cookie.getName();
                    if (!StringUtils.isEmpty(cookieName)) {
                        if (cookieName.equals("groupId_show")) {
                            groupId_show = cookie.getValue();
                        } else if (cookieName.equals("env") && isAdmin) {
                            env = Byte.valueOf(cookie.getValue());
                        }
                    }// end if
                }//end for
            }

            if (isAdmin) {

                if (!StringUtils.isEmpty(envToSet)) {
                    env = envToSet;
                    Cookie cookie = new Cookie("env", env + "");
                    response.addCookie(cookie);
                } else {
                    Cookie cookie = new Cookie("env", env + "");
                    response.addCookie(cookie);
                }

            } else {
                Cookie cookie = new Cookie("env", env + "");
                response.addCookie(cookie);
            }


            //api
            List<Api> allApi = null;

            if (StringUtils.isEmpty(groupId_show)) {
                allApi = apiService.getAllByEnv(env);
            }


            if (null != allApi) {
                Collections.sort(allApi, new Comparator<Api>() {
                    @Override
                    public int compare(Api arg1, Api arg2) {
                        if (org.springframework.util.StringUtils.isEmpty(arg1.getCreatedTime()) ||
                                org.springframework.util.StringUtils.isEmpty(arg2.getCreatedTime())) { // 防止脏数据
                            return 0;
                        }
                        return arg2.getCreatedTime().compareTo(arg1.getCreatedTime()); // 按时间逆序排序
                    }
                });
            }
            //group
            Map<String, Object> groupMap = groupService.getGroupTreeById(-1);

            results.put("user", userName);
            Cookie cookie = new Cookie("isAdmin", isAdmin + "");
            response.addCookie(cookie);

            results.put("apilists", allApi);
            results.put("groupIdToName", groupService.getAllNameIdMapping());
            results.put("groupMap", groupMap);
            results.put("env", env);


        } catch (Exception e) {
            logger.error("get api list error", e);
            return new ModelAndView("redirect:/unifyerror", "cause", "error when get api list");
        }

        return new ModelAndView("apilist", "results", results);
    }

    @RequestMapping(value = "/getapi/by/groupid", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getApiByGroup(@RequestParam("groupid") Integer groupid,
                                @RequestParam(value = "env", required = false, defaultValue = "1") Byte env) {
        ApiResult<Object> apiResult = new ApiResult<>(0, "success");

        try {
            List<Api> list = null;
            if (groupid == null || groupid == 1) {
                list = apiService.getAllByEnv(env);
            } else {
                //list = apiService.getByGroupIdAndEnv(groupid,env);
                list = apiService.getAllByGroupIdAndEnv(groupid, env);
            }
            Collections.sort(list, new Comparator<Api>() {
                @Override
                public int compare(Api arg1, Api arg2) {
                    if (org.springframework.util.StringUtils.isEmpty(arg1.getCreatedTime()) || org.springframework.util.StringUtils.isEmpty(arg2.getCreatedTime())) { // 防止脏数据
                        return 0;
                    }
                    return arg2.getCreatedTime().compareTo(arg1.getCreatedTime()); // 按时间逆序排序
                }
            });

            Map<String, Object> data = new HashMap<>();

            data.put("list", list);
            data.put("mapping", groupService.getAllNameIdMapping());

            apiResult.setData(JSONObject.toJSON(data));
        } catch (Exception e) {
            logger.info("根据groupid获取api发生异常", e);
            apiResult.setCode(9000);
            apiResult.setMessage("根据grupid获取api发生异常");
        }
        return apiResult.toString();
    }


}
