package com.zitech.gateway.apiconfig.controller;

import com.zitech.gateway.apiconfig.model.Admin;
import com.zitech.gateway.apiconfig.service.AdminService;
import com.zitech.gateway.cache.RedisOperate;
import com.zitech.gateway.console.CacheManager;
import com.zitech.gateway.console.InstanceMonitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@Controller
public class InstanceController {

    // 日志记录器
    private final static Logger logger = LoggerFactory.getLogger(InstanceController.class);

    // Resource 默认按照名称进行装配
    @Resource
    RedisOperate redisOperate;
    @Resource
    AdminService iCarmenUserService;

    /**
     * 每个实例当前JVM状态
     *
     * @param env 环境变量 1 dev, 2 test, 3 product
     * @return 实例详情展示页面
     */
    @RequestMapping("/instancedetail")
    public ModelAndView instanceDetail(@RequestParam("env") String env,
                                       HttpServletRequest request) {
        String userName = null;
        Map<String, Object> hashMap = new HashMap();
        hashMap.put("env", env);
        try {
            String userKey = request.getSession().getAttribute("username").toString();
            userName = redisOperate.getStringByKey(userKey);
            redisOperate.set("username", userName, 60 * 60); // 一小时
            hashMap.put("data", userName);

            InstanceMonitor instanceMonitor = InstanceMonitor.getInstance();
            Map<String, String> allInstanceInfo = instanceMonitor.readAllInstanceInfo();
            hashMap.put("instance", allInstanceInfo);

            //添加本地缓存表格头部
            List<String> allCacheNames = CacheManager.getInstance().getAllCacheNames();
            hashMap.put("cacheNames", allCacheNames);

        } catch (Exception e) {
            logger.warn("fail to get session", e);
        }
        if (null == userName) {
            return new ModelAndView("redirect:/unifyerror", "cause", "userName is null.");
        }
        Boolean isAdmin = isAdministrator(userName);
        hashMap.put("isAdmin", isAdmin);
        hashMap.put("user", userName);
        return new ModelAndView("instancedetail", "results", hashMap);
    }

    public Boolean isAdministrator(String userName) {

        try {
            List<Admin> user = iCarmenUserService.getByUserName(userName);
            for (Admin carmenUser : user) {
                if (1 == carmenUser.getUserGroup()) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("can not get uesrs.", e);
        }
        return false;
    }
}
