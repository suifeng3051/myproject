package com.zitech.gateway.apiconfig.controller;

import com.zitech.gateway.apiconfig.model.Admin;
import com.zitech.gateway.apiconfig.model.Api;
import com.zitech.gateway.apiconfig.model.Param;
import com.zitech.gateway.apiconfig.model.Serve;
import com.zitech.gateway.apiconfig.service.AdminService;
import com.zitech.gateway.apiconfig.service.ApiService;
import com.zitech.gateway.apiconfig.service.ParamService;
import com.zitech.gateway.apiconfig.service.ServeService;
import com.zitech.gateway.cache.RedisOperate;
import com.zitech.gateway.common.ApiResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by hy on 16-5-4.
 */
@Controller
public class RecoverApiController {
    private final static Logger logger = LoggerFactory.getLogger(RecoverApiController.class);

    // Resource 默认按照名称进行装配
    @Resource
    RedisOperate redisOperate;
    @Resource
    private ApiService apiService;

    @Resource
    private ServeService serveService;

    @Resource
    private ParamService paramService;

    @Resource
    private AdminService adminService;


    @RequestMapping("/recoverapi")
    public ModelAndView recoverApi(@RequestParam("env") String env,
                                   HttpServletRequest request) {


        String userName = null;


        List<Api> deletedApi = null;
        try {
            userName = adminService.getUserNameFromSessionAndRedis(request);
            if (null == userName) {
                return new ModelAndView("redirect:/unifyerror", "cause", "Fail to get user name");
            }

            deletedApi = apiService.getDeletedApi();
            if (null != deletedApi) {
                Collections.sort(deletedApi, new Comparator<Api>() {
                    @Override
                    public int compare(Api arg1, Api arg2) {
                        if (StringUtils.isEmpty(arg1.getCreatedTime()) || StringUtils.isEmpty(arg2.getCreatedTime())) { // 防止脏数据
                            return 0;
                        }
                        return arg2.getCreatedTime().compareTo(arg1.getCreatedTime()); // 按时间逆序排序
                    }
                });
            }
        } catch (Exception e) {
            logger.error("fail to get deleted api", e);
        }

        Map<String, Object> results = new HashMap<>();
        results.put("apilists", deletedApi);
        results.put("user", userName);
        results.put("env", env);
        results.put("isAdmin", adminService.isAdmin(userName));
        return new ModelAndView("recoverapi", "results", results);
    }

    /**
     * 恢复删除的api
     * @param ids
     * @param request
     * @return
     */
    @RequestMapping(value = "/recoverApis", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String recoverApis(@RequestParam("ids") String ids,
                              HttpServletRequest request) {
        ApiResult<Boolean> apiResult = new ApiResult<>(0, "success");

        String userName = adminService.getUserNameFromSessionAndRedis(request);
        if (null == userName) {
            apiResult.setCode(2);
            apiResult.setMessage("未查找到用户信息");
            return apiResult.toString();
        }
        List<Admin> users = adminService.getByUserName(userName);
        if (users.size() == 0) {
            apiResult.setCode(2);
            apiResult.setMessage("未查找到用户信息");
            return apiResult.toString();
        }
        Integer userid = users.get(0).getId();

        String[] idArray = ids.split(",");
        Integer numbers = idArray.length;
        Integer cnt = 0;
        try {
            for (String id : idArray) {
                Integer apiId = Integer.valueOf(id);
                Api api = apiService.getApiById(apiId);
                //检测要恢复的api是否已经存在同名的api
                if(!apiService.checkApi(api.getNamespace(), api.getMethod(), api.getVersion(), api.getEnv()))
                {
                    continue;
                }
                if (api != null) {
                    api.setUpdatedTime(new Date());
                    api.setDeleted((byte) 0);
                    api.setUpdatedId(userid);
                    apiService.updateApi(api);
                }

                Serve serve = serveService.getByApiId(apiId);
                if (serve != null) {
                    serve.setDeleted((byte) 0);
                    serve.setUpdatedId(userid);
                    serve.setUpdatedTime(new Date());
                    serveService.updateServe(serve);
                }

                Param param = paramService.getByApiId(apiId);
                if (param != null) {
                    param.setDeleted((byte) 0);
                    param.setUpdatedTime(new Date());
                    param.setUpdatedId(userid);
                    paramService.updateParam(param);
                }
                cnt++;
            }
        } catch (Exception e) {
            apiResult.setCode(3);
            apiResult.setMessage("恢复api发生异常");
            return apiResult.toString();
        }
        if (numbers!= cnt) {
            apiResult.setCode(4);
            apiResult.setMessage("因api已经存在，有部分api未恢复");
        }

        return apiResult.toString();
    }

    /**
     *
     * 物理删除api
     * @param ids
     * @param request
     * @return
     */
    @RequestMapping(value = "/deleteApis", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String deleteApis(@RequestParam("ids") String ids,
                              HttpServletRequest request) {
        ApiResult<Boolean> apiResult = new ApiResult<>(0, "success");

        String[] idArray = ids.split(",");
        try {
            for (String id : idArray) {
                Integer apiId = Integer.valueOf(id);
                Api api = apiService.getApiById(apiId);
                if (api != null) {
                    apiService.deleteApiRealById(api.getId());
                }

                Serve serve = serveService.getByApiId(apiId);
                if (serve != null) {
                    serveService.deleteServeRealById(serve.getId());
                }

                Param param = paramService.getByApiId(apiId);
                if (param != null) {
                    paramService.deleteParamRealById(param.getId());
                }
            }
        } catch (Exception e) {
            apiResult.setCode(3);
            apiResult.setMessage("删除api发生异常");
            return apiResult.toString();
        }

        return apiResult.toString();
    }
}
