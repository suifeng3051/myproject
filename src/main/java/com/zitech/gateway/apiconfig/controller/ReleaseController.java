package com.zitech.gateway.apiconfig.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zitech.gateway.apiconfig.model.Api;
import com.zitech.gateway.apiconfig.service.AdminService;
import com.zitech.gateway.apiconfig.service.ApiService;
import com.zitech.gateway.apiconfig.service.ReleaseService;
import com.zitech.gateway.cache.RedisOperate;
import com.zitech.gateway.common.ApiResult;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by dingdongsheng on 15/9/5.
 */
@Controller
public class ReleaseController {

    // 日志记录器
    private final static Logger logger = LoggerFactory.getLogger(ReleaseController.class);

    // Resource 默认按照名称进行装配
    @Resource
    ApiService apiService;
    @Resource
    AdminService adminService;
    @Resource
    RedisOperate redisOperate;
    @Autowired
    private ReleaseService releaseService;

    @RequestMapping("/release")
    public ModelAndView release(@RequestParam(value="env", defaultValue = "1") byte env,
                                HttpServletRequest request,
                                HttpServletResponse response) {

        String userName = adminService.getUserNameFromSessionAndRedis(request);

        if(null == userName) {
            return new ModelAndView("redirect:/unifyerror", "cause", "Fail to get user name");
        }

        List<Api> apiList = null;
        try {
            apiList = apiService.getAllByEnv(env);
            if(null != apiList) {
                Collections.sort(apiList, new Comparator<Api>() {
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
            logger.error("can not get api config", e);
        }

        Map<String, Object> results = new HashMap<>();
        results.put("apilists", apiList);
        results.put("user", userName);
        results.put("env", env);
        Boolean isAdmin = adminService.isAdmin(userName);
        results.put("isAdmin", isAdmin);
        return new ModelAndView("release", "results", results);

    }



    @RequestMapping(value = "/releasedownload", produces="application/json;charset=utf-8")
    public ResponseEntity<String> releasedownload(@RequestParam("ids") String ids,
                             @RequestParam("toEnv") byte toEnv) throws IOException {

        List<JSONObject> list_Info = new ArrayList<>();


        if(!StringUtils.isEmpty(ids)){

            if(ids.contains(",")){
                String[] idArray = ids.split(",");
                for(String id:idArray){
                    try{
                        list_Info.add(releaseService.getDownloadInfo(id));
                    }catch (Exception e){
                        throw new IOException("ID格式不正确！无法生成下载文件!");
                    }
                }
            }else{
                list_Info.add(releaseService.getDownloadInfo(ids));
            }

        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        headers.setContentDispositionFormData("attachment", "release.txt");

        String downloadStr = JSONArray.toJSONString(list_Info, SerializerFeature.DisableCheckSpecialChar);

        //JSONArray array = JSONArray.parseArray(downloadStr);

        downloadStr =new String(downloadStr.getBytes("utf-8"), "ISO8859-1");

        return new ResponseEntity<String>(downloadStr,
                headers, HttpStatus.CREATED);

    }

    @RequestMapping(value = "/releaseupload", produces="application/json;charset=utf-8")
    public String releaseupload(@RequestParam("file") MultipartFile file) {

        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();



             } catch (Exception e) {
                e.printStackTrace();
             }
        } else {
            return "";
        }

        //JSONArray array = JSONArray.parseArray(downloadStr);

        return null;
    }


}
