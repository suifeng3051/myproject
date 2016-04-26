package com.zitech.gateway.apiconfig.controller;

import com.alibaba.fastjson.JSON;
import com.zitech.gateway.apiconfig.model.Api;
import com.zitech.gateway.apiconfig.service.AdminService;
import com.zitech.gateway.common.ApiResult;
import com.zitech.gateway.oauth.model.Client;
import com.zitech.gateway.oauth.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hy on 16-4-26.
 */
@Controller
public class ClientController {
    private final static Logger logger = LoggerFactory.getLogger(ClientController.class);

    @Autowired
    private ClientService clientService;

    @Autowired
    private AdminService adminService;


    /**
     * 根据指定id查找OauthClient
     * @param id
     * @return
     */
    @RequestMapping(value = "/getClientById", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getClientByid(Integer id) {
        ApiResult<Client> apiResult = new ApiResult<>(0, "success");

        try {
            Client client = clientService.getById(id);
            if (client == null) {
                logger.error("根据指定的id未查找到Client");
                apiResult.setCode(9001);
                apiResult.setMessage("根据指定的id未查找到Client");
            } else {
                apiResult.setData(client);
            }
        } catch (Exception e) {
            logger.info("查找client发生异常",e);
            apiResult.setCode(9002);
            apiResult.setMessage("查找Client出错"+e.getMessage());
        }

        return apiResult.toString();
    }

    /**
     * 根据id删除oauth client
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete/client", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String deleteOauthClient(Integer id) {
        ApiResult<Boolean> apiResult = new ApiResult<>(0, "success");
        try {
            clientService.deleteById(id);
        } catch (Exception e) {
            logger.error("删除Client发生异常", e);
            apiResult.setCode(9003);
            apiResult.setMessage("删除Client发生异常"+e.getMessage());
        }
        return apiResult.toString();
    }


    /**
     *
     * @return
     */
    @RequestMapping(value="/get/client", produces="application/json;charset=utf-8")
    @ResponseBody
    public String getOauthClient()
    {
        ApiResult<Map<String, Object>> apiResult = new ApiResult<>(0, "success");

        Map<String, Object> results = new HashMap<>();
        try {
            List<Client> all = clientService.getAll();
            results.put("allClient", all);
            results.put("size", all.size());
            apiResult.setData(results);
        } catch (Exception e) {
            logger.error("查询Client发生异常",e);
            apiResult.setMessage("查询Client发生异常");
            apiResult.setCode(9004);
        }

        return JSON.toJSONString(results);
    }

    @RequestMapping("/client")
    public ModelAndView client(@RequestParam(value = "env",required = false, defaultValue = "1") Byte env,
            HttpServletRequest request) {

        String userName = adminService.getUserNameFromSessionAndRedis(request);
        if (null == userName) {
            return new ModelAndView("redirect:/unifyerror", "cause", "userName is null.");
        }
        Map<String, Object> hashMap = new HashMap();
        hashMap.put("env", env);
        hashMap.put("user", userName);
        hashMap.put("isAdmin", adminService.isAdmin(userName));

        return new ModelAndView("client", "results", hashMap);
    }
}
