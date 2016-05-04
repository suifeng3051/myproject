package com.zitech.gateway.apiconfig.controller;

import com.alibaba.fastjson.JSON;
import com.zitech.gateway.apiconfig.model.Admin;
import com.zitech.gateway.apiconfig.service.AdminService;
import com.zitech.gateway.cache.RedisOperate;
import com.zitech.gateway.console.CacheManager;
import com.zitech.gateway.oauth.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


@Controller
public class CacheManageController {

    // 日志记录器
    private final static Logger logger = LoggerFactory.getLogger(CacheManageController.class);

    // Resource 默认按照名称进行装配
    @Resource
    RedisOperate redisOperate;

    @Autowired
    private AdminService adminService;

    /**
     * 管理cache
     * @param env 环境变量 1 dev, 2 test, 3 product
     * @param request
     * @return 管理页面cache
     */
    @RequestMapping("/cachemanage")
    public ModelAndView cacheManage(@RequestParam("env") String env,
                                    HttpServletRequest request) {

        Map<String, Object> hashMap = new HashMap();
        hashMap.put("env", env);
        try {
            // 获取用户名
            String userName = adminService.getUserNameFromSessionAndRedis(request);
            if(null == userName) {
                return new ModelAndView("redirect:/unifyerror", "cause", "userName is null.");
            }
            hashMap.put("isAdmin", adminService.isAdmin(userName));
            hashMap.put("user", userName);
            hashMap.put("data", userName);
            CacheManager cacheManager = CacheManager.getInstance();
            Map<String, String> allCacheNames = cacheManager.readNodes();

            TreeMap<String, String> cacheNames = new TreeMap<>();
            for (Map.Entry<String, String> entry : allCacheNames.entrySet()) {
                cacheNames.put(entry.getKey(), entry.getValue());
            }

            hashMap.put("allCacheNames", cacheNames);

        } catch (Exception e) {
            logger.warn("fail to get session", e);
        }


        return new ModelAndView("cachemanage", "results", hashMap);
    }



    /**
     * 清除对应实例的缓存
     * @param names 实例名
     * @return 成功返回success，失败返回fail
     */
    @RequestMapping(value = "/clearcache", produces="application/json;charset=utf-8")
    @ResponseBody
    public String clearCache(@RequestParam("names") String names) {
        String status = "fail";
        String[] allNames = names.split(",");
        List<String> instance = Arrays.asList(allNames);
        logger.info(names);
        try {
            if(null != instance && instance.size() > 0) {
                if(instance.contains(Constants.CACHE_NAME_ACCESS_TOKEN)) {
                    redisOperate.delKeys(Constants.CACHE_ACCESS_TOKEN_KEY_PATTERN);
                }
            }
            CacheManager cacheManager = CacheManager.getInstance();
            cacheManager.updateNodes(instance);
            status = "success";
        } catch (Exception e) {
            logger.error("can not clear cache.", e);
        }

        try {
            status = JSON.toJSONString(status);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }

        return status;
    }

    /**
     * 加载对应实例的缓存
     * @param names 实例名
     * @return 成功返回success，失败返回fail
     */
    @RequestMapping(value = "/preloadcache", produces="application/json;charset=utf-8")
    @ResponseBody
    public String preLoadCache(@RequestParam("names") String names) {
        String status = "fail";
        String[] allNames = names.split(",");
        List<String> instance = Arrays.asList(allNames);
        try {
            CacheManager cacheManager = CacheManager.getInstance();
            //添加缓存预加载
            cacheManager.updatePreNodes(instance);
            status = "success";
        } catch (Exception e) {
            logger.error("can not clear cache.", e);
        }

        try {
            status = JSON.toJSONString(status);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }

        return status;
    }
}
