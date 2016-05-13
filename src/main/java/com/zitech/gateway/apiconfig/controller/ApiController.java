package com.zitech.gateway.apiconfig.controller;

import com.alibaba.fastjson.JSONObject;
import com.zitech.gateway.apiconfig.model.Admin;
import com.zitech.gateway.apiconfig.model.Api;
import com.zitech.gateway.apiconfig.model.Group;
import com.zitech.gateway.apiconfig.model.Param;
import com.zitech.gateway.apiconfig.model.Serve;
import com.zitech.gateway.apiconfig.service.AdminService;
import com.zitech.gateway.apiconfig.service.ApiService;
import com.zitech.gateway.apiconfig.service.GroupService;
import com.zitech.gateway.apiconfig.service.ParamService;
import com.zitech.gateway.apiconfig.service.ServeService;
import com.zitech.gateway.cache.RedisOperate;
import com.zitech.gateway.common.ApiResult;
import com.zitech.gateway.gateway.Constants;
import com.zitech.gateway.param.ParamHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by hy on 16-4-26.
 */
@Controller
public class ApiController {
    private final static Logger logger = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    private RedisOperate redisOperate;

    @Autowired
    private GroupService groupService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private ApiService apiService;
    @Autowired
    private ServeService serveService;
    @Autowired
    private ParamService paramService;


    @RequestMapping("/createapi")
    public ModelAndView createApi(@RequestParam(value = "env", defaultValue = "1") Byte env,
                                  @RequestParam("group") String group,
                                  @RequestParam(value = "edit", required = false, defaultValue = "0") Integer edit,
                                  @RequestParam(value = "detail", required = false, defaultValue = "0") Integer detail,
                                  @RequestParam(value = "apiId", required = false) Integer apiId,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        String userName = adminService.getUserNameFromSessionAndRedis(request);
        if (null == userName) {
            return new ModelAndView("redirect:/unifyerror", "cause", "Fail to get user name");
        }

        Map<String, Object> results = new HashMap<>();

        Api apiModel = apiService.getApiById(apiId);
        Serve serveModel = serveService.getByApiId(apiId);
        Param paramModel = paramService.getByApiId(apiId);

        if (apiModel != null) {
            results.put("edit", "1");
        } else {
            results.put("edit", "0");
        }

        results.put("user", userName);
        results.put("apiType", "1");
        //results.put("env", env);
        results.put("api", apiModel);
        results.put("serve", serveModel);
        results.put("param", paramModel);
        results.put("detail", detail);
        List<Group> groupList = null;
        try {
            groupList = groupService.getAll();
        } catch (Exception e) {
            logger.info("fail to get group info", e);
        }
        results.put("grouplist", groupList);
        results.put("isAdmin", adminService.isAdmin(userName));
        results.put("group", group);
        return new ModelAndView("createapi", "results", results);
    }

    /**
     * 查询method映射是否存在
     *
     * @param namespace API命名空间
     * @param name      API名字
     * @param version   API版本
     * @param env       环境，1：开发环境，2：测试，3：生产
     * @return 有映射返回success，没有映射返回fail
     */
    @RequestMapping(value = "/checkapi", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String checkApiMethodMapping(@RequestParam("namespace") String namespace,
                                        @RequestParam("name") String name,
                                        @RequestParam("version") Integer version,
                                        @RequestParam("env") Byte env) {

        ApiResult<String> apiResult = new ApiResult<>(0, "success");

        if (!apiService.checkApi(namespace, name, version, env)) {
            apiResult.setCode(1);
            apiResult.setMessage("fail");
        }

        return apiResult.toString();

    }


    @RequestMapping(value = "/saveResult", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
    @ResponseBody
    public String saveResult(@RequestParam("apiObj") String apiObj,
                             @RequestParam("serviceObj") String serviceObj,
                             @RequestParam("paramObj") String paramObj,
                             @RequestParam("env") Byte env) {

        int code = 0;
        String message = "success";
        boolean flag = false;
        ApiResult<String> apiResult = new ApiResult<>(0, "success");
        try {
            flag = apiService.saveResult(apiObj, serviceObj, paramObj, env);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("保存API是出错！\r apiObj:" + apiObj + "  \r serviceObj:" + serviceObj + " \r paramObj:" + paramObj, e);
            code = 1;
            message = "fail";
            apiResult.setData("请检查对象传值是否正确！");
        }

        if (!flag) {
            code = 1;
            message = "fail";
        }

        apiResult.setCode(code);
        apiResult.setMessage(message);

        return apiResult.toString();

    }


    @RequestMapping(value = "/getServeInner", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
    @ResponseBody
    public String getServeInner() {
        ApiResult<Map<String, String>> apiResult = new ApiResult<>(0, "success", Constants.contextMap);
        return apiResult.toString();
    }

    @RequestMapping(value = "/validateJsonStr", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
    @ResponseBody
    public String validateJsonStr(@RequestParam("jsonStr") String jsonStr,
                                  @RequestParam("struct") String struct) {

        int code = 0;
        String message = "success";
        String data = "参数验证通过！";

        try {
            ParamHelper.validate(jsonStr, ParamHelper.buildTree(struct));
        } catch (Exception e) {
            e.printStackTrace();
            code = 1;
            message = "fail";
            data = e.getMessage();
        }

        return new ApiResult<>(code, message, data).toString();
    }

    @RequestMapping(value = "/uptAvail", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
    @ResponseBody
    public String uptAvail(@RequestParam("update") String update) {


        JSONObject obj = JSONObject.parseObject(update);

        if (!obj.containsKey("id") || !obj.containsKey("avail") || !obj.containsKey("env")) {

            return new ApiResult<>(1, "更新失败！传值不完成！").toString();
        }


        if (apiService.uptAvail(obj.getInteger("id"), obj.getInteger("avail"), obj.getByte("env"))) {
            return new ApiResult<>(0, "success").toString();
        } else {
            return new ApiResult<>(1, "更新失败！").toString();
        }

    }

    @RequestMapping(value = "/deleteapi", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String uptAvail(@RequestParam(value = "id", required = true) Integer apiId,
                           HttpServletRequest request) {

        ApiResult<Boolean> apiResult = new ApiResult<>(0, "success");

        try {
            String userName = adminService.getUserNameFromSessionAndRedis(request);
            if (null == userName) {
                apiResult.setCode(2);
                apiResult.setMessage("未查询到用户信息");
                return apiResult.toString();
            }
            List<Admin> byUserName = adminService.getByUserName(userName);
            if (byUserName.size() == 0) {
                apiResult.setCode(3);
                apiResult.setMessage("未查询到用户信息");
                return apiResult.toString();
            }
            Integer userid = byUserName.get(0).getId();

            Api api = apiService.getApiById(apiId);
            if (api != null) {
                apiService.deleteApiById(apiId, userid);
            }
            Serve serve = serveService.getByApiId(apiId);
            if (serve != null) {
                serveService.deleteServeByApiId(apiId, userid);
            }
            Param param = paramService.getByApiId(apiId);
            if (param != null) {
                paramService.deleteParamByApiId(apiId, userid);
            }
        } catch (Exception e) {
            logger.info("删除api发生异常", e);
            apiResult.setCode(1);
            apiResult.setMessage("删除api发生异常");
        }


        return apiResult.toString();
    }

}
