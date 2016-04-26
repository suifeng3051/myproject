package com.zitech.gateway.apiconfig.controller;

import com.zitech.gateway.apiconfig.model.Admin;
import com.zitech.gateway.apiconfig.service.AdminService;
import com.zitech.gateway.cache.RedisOperate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Controller
public class MonitorController {
    // 日志记录器
    private final static Logger logger = LoggerFactory.getLogger(MonitorController.class);

    @Resource
    RedisOperate redisOperate;
    @Resource
    AdminService iCarmenUserService;

    /**
     * 监控页面
     * @param request
     * @return
     */
    @RequestMapping("/monitor")
    public ModelAndView displayMonitorData(@RequestParam(value = "namespace", required = false) String namespace,
                                           @RequestParam(value = "name", required = false) String name,
                                           @RequestParam(value = "version", required = false) String version,
                                           @RequestParam(value = "env", defaultValue = "1") String env,
                                           HttpServletRequest request) {
        String userName = null;
        Map<String, Object> results = new HashMap<>();
        try {
            String userKey = request.getSession().getAttribute("username").toString();
            userName = redisOperate.getStringByKey(userKey);
            redisOperate.set("username", userName, 60*60); // 一小时
            results.put("data", userName);
            if(!StringUtils.isEmpty(namespace)) {
                String api = namespace + "." + name + "." + version;
                results.put("apiName", api);
            }


        } catch (Exception e) {
            logger.warn("fail to get session", e);
        }
        if(null == userName) {
            return new ModelAndView("redirect:/unifyerror", "cause", "test");
        }
        results.put("env", env);
        Boolean isAdmin = isAdministrator(userName);
        results.put("isAdmin", isAdmin);
        results.put("user", userName);
        return new ModelAndView("monitor", "results", results);
    }

    public Boolean isAdministrator(String userName) {

        try {
            List<Admin> user = iCarmenUserService.getByUserName(userName);
            for(Admin carmenUser : user) {
                if(1 == carmenUser.getUserGroup()) {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("can not get uesrs.", e);
        }
        return false;
    }
}
