package com.zitech.gateway.apiconfig.controller;

import com.alibaba.fastjson.JSONObject;
import com.zitech.gateway.apiconfig.model.Api;
import com.zitech.gateway.apiconfig.model.Group;
import com.zitech.gateway.apiconfig.model.Param;
import com.zitech.gateway.apiconfig.model.Serve;
import com.zitech.gateway.apiconfig.service.*;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by hy on 16-4-26.
 */
@Controller
public class CreateApiController {
    private final static Logger logger = LoggerFactory.getLogger(CreateApiController.class);

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
    public ModelAndView createApi(@RequestParam(value = "env", defaultValue="1") Byte env,
                                  @RequestParam("group") String group,
                                  @RequestParam(value = "edit", required = false, defaultValue = "0") Integer edit,
                                  @RequestParam(value = "apiId", required = false) Integer apiId,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        String userName = adminService.getUserNameFromSessionAndRedis(request);
        if(null == userName) {
            return new ModelAndView("redirect:/unifyerror", "cause", "Fail to get user name");
        }

        Map<String, Object> results = new HashMap<>();

        Api apiModel = apiService.getApiById(apiId);
        Serve serveModel = serveService.getByApiId(apiId);
        Param paramModel = paramService.getByApiId(apiId);

        if(apiModel!=null){
            results.put("edit","1");
        }else{
            results.put("edit","0");
        }

        results.put("user", userName);
        results.put("apiType","1");
        results.put("env", env);
        results.put("api", apiModel);
        results.put("serve", serveModel);
        results.put("param", paramModel);
        List<Group> groupList = null;
        try {
            groupList = groupService.getAll();
        } catch (Exception e) {
            logger.info("fail to get group info", e);
        }
        results.put("grouplist", groupList);
//        if(1 == edit) {
//            CarmenApiMethodMapping carmenApiMethodMapping = null;
//            try {
//                carmenApiMethodMapping = iCarmenApiMethodMappingService.getByApiId(apiId, env);
//
//                if(null == carmenApiMethodMapping) { // 如果找不到直接跳转到错误页面
//                    return new ModelAndView("redirect:/unifyerror", "cause", "Fail to get methodId by apiId.");
//                }
//                Long methodId = carmenApiMethodMapping.getServiceMethodId();
//                CarmenServiceMethod carmenServiceMethod = iCarmenServiceMethodService.getById(methodId);
//                if(null == carmenServiceMethod) {
//                    return new ModelAndView("redirect:/unifyerror", "cause", "Fail to get method object by methodId.");
//                }
//                String service = carmenServiceMethod.getName();
//                String method = carmenServiceMethod.getMethod();
//                String version = carmenServiceMethod.getVersion();
//                CarmenApiDataMigrate carmenApiDataMigrate =  icarmenDataMigrateService.dataExport(service, method, version, env);
//                results.put("api", carmenApiDataMigrate.getApi());
//                if(2 == carmenApiDataMigrate.getApi().getApiType()) { // 2表示PHP
//                    results.put("apiType","2");
//                }
//                // 对方法参数排序
//                List<CarmenServiceMethodParam> methodParamLists = carmenApiDataMigrate.getServiceMethodParamList();
//                Collections.sort(methodParamLists, new Comparator<CarmenServiceMethodParam>() {
//                    @Override
//                    public int compare(CarmenServiceMethodParam arg0, CarmenServiceMethodParam arg1) {
//                        if(StringUtils.isEmpty(arg0.getSequence()) || StringUtils.isEmpty(arg1.getSequence())) {
//                            return 0;
//                        }
//                        return arg0.getSequence() - arg1.getSequence();
//                    }
//                });
//                // 对API参数排序
//                List<CarmenApiParam> apiParamList = carmenApiDataMigrate.getApiParamList();
//                Collections.sort(apiParamList, new Comparator<CarmenApiParam>() {
//                    @Override
//                    public int compare(CarmenApiParam arg0, CarmenApiParam arg1) {
//                        if(StringUtils.isEmpty(arg0.getSequence()) || StringUtils.isEmpty(arg1.getSequence())) {
//                            return 0;
//                        }
//                        return arg0.getSequence() - arg1.getSequence();
//                    }
//                });
//                // 对参数参数映射排序
//                List<CarmenParamMapping> paramMappingList = carmenApiDataMigrate.getParamMappingList();
//                Collections.sort(paramMappingList, new Comparator<CarmenParamMapping>() {
//                    @Override
//                    public int compare(CarmenParamMapping arg0, CarmenParamMapping arg1) {
//                        if(StringUtils.isEmpty(arg0.getSequence()) || StringUtils.isEmpty(arg1.getSequence())) {
//                            return 0;
//                        }
//                        return arg0.getSequence() - arg1.getSequence();
//                    }
//                });
//                results.put("apiParamList", carmenApiDataMigrate.getApiParamList());
//                results.put("method", carmenApiDataMigrate.getServiceMethod());
//                results.put("methodParamList", methodParamLists);
//                results.put("structure", carmenApiDataMigrate.getStructList());
//                results.put("methodMapping", carmenApiDataMigrate.getApiMethodMapping());
//                results.put("paramMapping", carmenApiDataMigrate.getParamMappingList());
//                results.put("edit","1");
//
//                results.put("isAdmin", isAdmin);
//                results.put("group", group);
//                return new ModelAndView("createapi", "results", results);
//            } catch (Exception e) {
//                logger.error("fail to get api when status is edited.", e);
//            }
//
//        }
//
//        Boolean isAdmin = isAdministrator(userName);
        //results.put("isAdmin", isAdmin);
        results.put("group", group);
        return new ModelAndView("createapi", "results", results);
    }

    /**
     * 查询method映射是否存在
     * @param namespace API命名空间
     * @param name API名字
     * @param version API版本
     * @param env 环境，1：开发环境，2：测试，3：生产
     * @return 有映射返回success，没有映射返回fail
     */
    @RequestMapping(value = "/checkapi", produces="application/json;charset=utf-8")
    @ResponseBody
    public String checkApiMethodMapping(@RequestParam("namespace") String  namespace,
                                        @RequestParam("name") String name,
                                        @RequestParam("version")  Integer version,
                                        @RequestParam("env") Byte env) {

        ApiResult<String> apiResult = new ApiResult<>(0,"success");

        if(!apiService.checkApi(namespace,name,version,env)){
            apiResult.setCode(1);
            apiResult.setMessage("fail");
        }

        return apiResult.toString();

    }


    @RequestMapping(value = "/saveResult", produces="application/json;charset=utf-8",method= RequestMethod.POST)
    @ResponseBody
    public String saveResult(@RequestParam("apiObj") String  apiObj,
                             @RequestParam("serviceObj") String serviceObj,
                             @RequestParam("paramObj")  String paramObj,
                             @RequestParam("env") Byte env) {

        int code = 0;
        String message = "success";
        boolean flag = false;
        ApiResult<String> apiResult = new ApiResult<>(0,"success");
        try {
            flag = apiService.saveResult(apiObj,serviceObj,paramObj, env);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("保存API是出错！\r apiObj:"+apiObj+"  \r serviceObj:"+serviceObj+" \r paramObj:"+paramObj,e);
            code=1;
            message = "fail";
            apiResult.setData("请检查对象传值是否正确！");
        }

        if(!flag){
            code =1;
            message = "fail";
        }

        apiResult.setCode(code);
        apiResult.setMessage(message);

        return apiResult.toString();

    }


    @RequestMapping(value = "/getServeInner", produces="application/json;charset=utf-8",method= RequestMethod.GET)
    @ResponseBody
    public String getServeInner() {
        ApiResult<Map<String, String>> apiResult = new ApiResult<>(0,"success", Constants.contextMap);
        return apiResult.toString();
    }

    @RequestMapping(value = "/validateJsonStr", produces="application/json;charset=utf-8",method= RequestMethod.GET)
    @ResponseBody
    public String validateJsonStr(@RequestParam("jsonStr") String  jsonStr,
                                  @RequestParam("struct") String  struct) {

        int code = 0;
        String message = "success";
        String data = "json验证通过！";

        try{
            ParamHelper.validate(jsonStr, ParamHelper.buildTree(struct));
        }catch (Exception e) {
            e.printStackTrace();
            code = 1;
            message = "fail";
            data = e.getMessage();
        }

        return  new ApiResult<>(code,message,data).toString();
    }

    @RequestMapping(value = "/uptAvail", produces="application/json;charset=utf-8",method= RequestMethod.POST)
    @ResponseBody
    public String uptAvail(@RequestParam("update") String  update) {


        JSONObject obj = JSONObject.parseObject(update);

        if(!obj.containsKey("id")||!obj.containsKey("avail")||!obj.containsKey("env")){

            return  new ApiResult<>(1,"更新失败！传值不完成！").toString();
        }


        if(apiService.uptAvail(obj.getInteger("id"),obj.getInteger("avail"),obj.getByte("env")) ){
            return  new ApiResult<>(0,"success").toString();
        }else{
            return  new ApiResult<>(1,"更新失败！").toString();
        }

    }





}
