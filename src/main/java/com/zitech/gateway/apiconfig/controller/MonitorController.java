package com.zitech.gateway.apiconfig.controller;

import com.zitech.gateway.apiconfig.service.AdminService;
import com.zitech.gateway.cache.RedisOperate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@Controller
public class MonitorController {
    // 日志记录器
    private final static Logger logger = LoggerFactory.getLogger(MonitorController.class);

    @Resource
    RedisOperate redisOperate;
    @Resource
    private AdminService adminService;

    /**
     * 监控页面
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
            userName = adminService.getUserNameFromSessionAndRedis(request);
            if (userName == null) {
                return new ModelAndView("redirect:/unifyerror", "cause", "test");
            }
            results.put("data", userName);
            if (!StringUtils.isEmpty(namespace)) {
                String api = namespace + "." + name + "." + version;
                results.put("apiName", api);
            }
        } catch (Exception e) {
            logger.warn("fail to get session", e);
        }

        results.put("env", env);
        results.put("isAdmin", adminService.isAdmin(userName));
        results.put("user", userName);
        return new ModelAndView("monitor", "results", results);
    }
}
