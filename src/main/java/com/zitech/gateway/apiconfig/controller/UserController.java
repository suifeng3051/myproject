package com.zitech.gateway.apiconfig.controller;

import com.alibaba.fastjson.JSON;
import com.zitech.gateway.apiconfig.model.Admin;
import com.zitech.gateway.apiconfig.service.AdminService;
import com.zitech.gateway.cache.RedisOperate;
import com.zitech.gateway.utils.AppUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@Controller
public class UserController {

    // 日志记录器
    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    // Resource 默认按照名称进行装配
    @Resource
    AdminService adminService;
    @Resource
    RedisOperate redisOperate;

    /**
     * 用户管理页面
     *
     * @return 用户管理页面
     */
    @RequestMapping("/user")
    public ModelAndView user(@RequestParam("env") String env,
                             HttpServletRequest request) {
        String userName = null;
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("env", env);
        try {
            userName = adminService.getUserNameFromSessionAndRedis(request);
            if (null == userName) {
                return new ModelAndView("redirect:/unifyerror", "cause", "userName is null.");
            }
            List<Admin> allList = adminService.getAllList();
            hashMap.put("data", userName);
            hashMap.put("allList", allList);
        } catch (Exception e) {
            logger.warn("fail to get session", e);
        }

        Boolean isAdmin = adminService.isAdmin(userName);
        hashMap.put("isAdmin", isAdmin);
        hashMap.put("user", userName);
        return new ModelAndView("user", "results", hashMap);
    }


    /**
     * 统一错误处理界面
     */
    @RequestMapping("/unifyerror")
    public ModelAndView unifyError() {

        return new ModelAndView("unifyError");
    }

    /**
     * 获取用户信息
     *
     * @param userGroup 用户组
     * @param userName  用户名
     * @return 成功返回用户信息，失败返回fail
     */
    @RequestMapping(value = "/getusers", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getUsers(@RequestParam(value = "userGroup", defaultValue = "") String userGroup,
                           @RequestParam("userName") String userName) {

        String status = "fail";

        try {
            List<Admin> user = adminService.getByUserName(userName);
            String result = JSON.toJSONString(user.get(0));
            return result;
        } catch (Exception e) {
            logger.error("can not get uesrs.", e);
        }


        try {
            status = JSON.toJSONString(status);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }

        return status;
    }

//    /**
//     * 判断当前登录用户是不是管理员
//     * @param userName 当前登录用户名
//     * @return 是管理员返回true，不是返回false
//     */
//    @RequestMapping(value = "/isadministrator", produces="application/text;charset=utf-8")
//    @ResponseBody
//    public Boolean isAdministrator(@RequestParam("userName") String userName) {
//
//        try {
//            List<Admin> user = adminService.getByUserName(userName);
//            for(Admin carmenUser : user) {
//                if(1 == carmenUser.getUserGroup()) {
//                    return true;
//                }
//            }
//
//        } catch (Exception e) {
//            logger.error("can not get uesrs.", e);
//        }
//        return false;
//    }


    /**
     * 删除用户
     *
     * @param id 用户id
     * @return 成功返回success，失败返回fail
     */
    @RequestMapping(value = "deleteuser", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String deleteUser(@RequestParam("id") String id) {
        String status = "fail";

        try {
            adminService.deleteById(Integer.valueOf(id));
            status = "success";
        } catch (NumberFormatException e) {
            logger.error("can not delete user.", e);
        } catch (Exception e) {
            logger.error("no session", e);
        }

        try {
            status = JSON.toJSONString(status);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }

        return status;
    }

    /**
     * 增加普通用户或者管理员
     *
     * @param userName  用户名
     * @param userGroup 用户所属组
     * @return 成功返回success，失败返回fail
     */
    @RequestMapping(value = "/adduser", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String addUser(@RequestParam("userName") String userName,
                          @RequestParam("userGroup") String userGroup,
                          HttpServletRequest request) {
        String status = "fail";
        try {
            if (adminService.getByUserName(userName).size() == 0) {
                Admin user = new Admin();
                user.setUserName(userName);
                user.setUserGroup(Integer.valueOf(userGroup));
                user.setCreateTime(new Date());
                user.setPassword(AppUtils.MD5("666666"));
                user.setIsDelete("n");
                String userKey = request.getSession().getAttribute("username").toString();
                userName = redisOperate.getStringByKey(userKey);
                user.setCreator(userName);

                adminService.insert(user);
                status = "success";
            }

        } catch (NumberFormatException e) {
            logger.error("can not delete user.", e);
        } catch (Exception e) {
            logger.error("no session", e);
        }

        try {
            status = JSON.toJSONString(status);
        } catch (Exception e) {
            logger.warn("fail to convert json", e);
        }

        return status;
    }

    @RequestMapping("/user/login")
    public ModelAndView userLogin(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("admin_login");
    }

    @RequestMapping(value = "/user/doLogin", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String doLogin(HttpServletRequest request, HttpServletResponse response, String username, String password) {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("result", false);
        objectMap.put("to", "/user/login");
        password = AppUtils.MD5(password);
        List<Admin> users = adminService.getByUserName(username);
        for (Admin u : users) {
            if (u.getPassword().equals(password)) {
                HttpSession httpSession = request.getSession(true);
                String nameKey = "gateway_login_" + username;
                httpSession.setAttribute("username", nameKey);
                redisOperate.set(nameKey, username, 60 * 60);
                objectMap.put("to", "/apilist");
                objectMap.put("result", true);
                return JSON.toJSONString(objectMap);
            }
        }
        return JSON.toJSONString(objectMap);
    }

    @RequestMapping("/user/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession httpSession = request.getSession(true);
        httpSession.removeAttribute("username");
        return "redirect:/user/login";
    }


    @RequestMapping("/updatepwd")
    public ModelAndView updatepwd(HttpServletRequest request, HttpServletResponse response, String env, Model model) {
        Map<String, Object> results = new HashMap<>();

        String userName = null;
        try {
            String userKey = request.getSession().getAttribute("username").toString();
            userName = redisOperate.getStringByKey(userKey);
            redisOperate.set("username", userName, 60 * 60); // 一小时
        } catch (Exception e) {
            logger.warn("fail to get session", e);
        }
        if (null == userName) {
            return new ModelAndView("redirect:/unifyerror", "cause", "test");
        }
        Boolean isAdmin = adminService.isAdmin(userName);
        results.put("isAdmin", isAdmin);
        results.put("user", userName);
        results.put("env", env);
        model.addAttribute("env", env);
        return new ModelAndView("update_pwd", "results", results);
    }


    @RequestMapping(value = "/user/dopwdUp", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String dopwdUp(HttpServletRequest request, HttpServletResponse response, String oldpwd, String password) {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("result", true);
        objectMap.put("message", "修改成功");
        HttpSession httpSession = request.getSession(true);
        String username = (String) httpSession.getAttribute("username");
        username = username.replaceAll("gateway_login_", "");
        List<Admin> users = adminService.getByUserName(username);
        if (users == null || users.size() <= 0) {
            objectMap.put("result", false);
            objectMap.put("message", "用户不存在");
            return JSON.toJSONString(objectMap);
        }
        Admin user = users.get(0);
        if (!AppUtils.MD5(oldpwd).equals(user.getPassword())) {
            objectMap.put("result", false);
            objectMap.put("message", "密码错误");
            return JSON.toJSONString(objectMap);
        }
        password = AppUtils.MD5(password);
        Admin newuser = new Admin();
        newuser.setPassword(password);
        newuser.setUserName(username);
        adminService.updatePwd(newuser);
        return JSON.toJSONString(objectMap);
    }
}
