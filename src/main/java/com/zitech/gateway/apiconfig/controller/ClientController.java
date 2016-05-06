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
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
     * 插入新的oauth client
     *
     * @param clientStr
     * @return
     */
    @RequestMapping(value="/add/client",produces = "application/json;charset=utf-8")
    @ResponseBody
    public String addOauthClient(@RequestParam("oauthclient") String clientStr)
    {
        ApiResult<String> apiResult = new ApiResult<>(0, "success");

        try{
            Client client = new Client();
            String ret = parseData(clientStr, client);
            if(!"success".equals(ret))
            {
                apiResult.setCode(9011);
                apiResult.setMessage(ret);
                return apiResult.toString();

            }

            //判断欲添加的oauth client是否已存在
            List<Client> all = clientService.getAll();
            for (Client oauthClient : all) {
                if(oauthClient.getClientName().equals(client.getClientName().trim()))
                {
                    apiResult.setCode(9012);
                    apiResult.setMessage("该名称的oauth client已经存在");
                    return apiResult.toString();
                }
            }

            //添加ID
            //获取到最大的id
            Long value = 0L;
            for (Client oauthClient : all) {
                Long aLong = Long.valueOf(oauthClient.getClientId());
                if(aLong > value)
                {
                    value=aLong;
                }
            }
            client.setClientId((value+1)+"");

            //添加client_secret
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            client.setClientSecret(uuid);

            clientService.save(client);
        }
        catch (Exception e)
        {
            logger.info("添加client出错",e);
            apiResult.setCode(9013);
            apiResult.setMessage("添加client出错");
        }
        return apiResult.toString();
    }

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
     * 获取所有的client
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

        return apiResult.toString();
    }

    /**
     * client page
     * @param env
     * @param request
     * @return
     */
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

    @RequestMapping(value="/update/client", produces="application/json;charset=utf-8")
    @ResponseBody
    public String updateOauthClient(@RequestParam("oauthclient") String clientStr)
    {
        ApiResult<Map<String, Object>> apiResult = new ApiResult<>(0, "success");

        try {
            String oauthclient = URLDecoder.decode(clientStr, "UTF-8");
            String[] splits = oauthclient.split("&");
            boolean b = false;
            Integer id= null;
            //查找id
            for (String split : splits) {
                String[] split1 = split.split("=");
                if("id".equals(split1[0])&& (split1.length == 2))
                {
                    b = true;
                    id = Integer.valueOf(split1[1]);
                }
            }
            if (!b) {
                apiResult.setCode(9009);
                apiResult.setMessage("id不能为空");
            }
            Client client = clientService.getById(id);
            if (client == null) {
                apiResult.setCode(9008);
                apiResult.setMessage("指定id的client不存在");
            }
            else {

                String ret = parseData(clientStr, client);
                if (!"success".equals(ret)) {
                    apiResult.setCode(9007);
                    apiResult.setMessage(ret);
                } else {
                    clientService.update(client);
                }
            }

        } catch (Exception e) {
            logger.info("更新client出错",e);
            apiResult.setCode(9006);
            apiResult.setMessage("更新client出错");
        }

        return apiResult.toString();
    }


    /**
     * 解析前端返回的字符为OpenOauthClients对象
     * @param clientStr
     * @param client
     * @return
     */
    private String parseData(String clientStr, Client client)
    {
        String status = "success";
        try {
            clientStr = URLDecoder.decode(clientStr, "UTF-8");
            String[] splits = clientStr.split("&");
            boolean b = false;
            //取出client_name
            for (String split : splits) {
                String[] split1 = split.split("=");
                if ("client_name".equals(split1[0]) && split1.length == 2) {
                    if(org.apache.commons.lang.StringUtils.isNotBlank(split1[1])) {
                        client.setClientName(split1[1]);
                        b = true;
                        break;
                    }
                }
            }
            if (!b) {
                return "fail: client_name不能为空";
            }
            //取出redirect_uri
            b = false;
            for (String split : splits) {
                String[] split1 = split.split("=");
                if ("redirect_uri".equals(split1[0]) && split1.length == 2) {
                    if(org.apache.commons.lang.StringUtils.isNotBlank(split1[1])) {
                        b = true;
                        client.setRedirectUri(split1[1]);
                        break;
                    }
                }
            }
            if (!b) {
                return "fail: redirect_uri不能为空";
            }
            //取出client_num
            b = false;
            for (String split : splits) {
                String[] split1 = split.split("=");
                if ("client_num".equals(split1[0]) && (split1.length == 2)) {
                    if (org.apache.commons.lang.StringUtils.isNumeric(split1[1]) &&
                            (!"0".equals(split1[1]))) {
                        b = true;
                        client.setClientNum(Byte.valueOf(split1[1]));
                        break;
                    } else {
                        return "fail: client_num必须为数字，且大于0";
                    }
                }
            }
            if (!b) {
                return "fail: client_num不能为空";
            }

            //取出 grant_types
            b = false;
            String grant_types = "";
            for (String split : splits) {
                String[] split1 = split.split("=");
                if ("grant_types".equals(split1[0]) && (split1.length == 2)) {
                    if(org.apache.commons.lang.StringUtils.isNotBlank(split1[1])) {
                        b = true;
                        if (!grant_types.equals("")) {
                            grant_types += " ";
                        }
                        grant_types += split1[1];
                    }
                }
            }
            if (!b) {
                return "fail: grant_types不能为空";
            }
            client.setGrantTypes(grant_types);

            //取出 default_scope
            b = false;
            String default_scope = "";
            for (String split : splits) {
                String[] split1 = split.split("=");
                if ("default_scope".equals(split1[0]) && (split1.length == 2)) {
                    if(org.apache.commons.lang.StringUtils.isNotBlank(split1[1])) {
                        b = true;
                        if (!default_scope.equals("")) {
                            default_scope += " ";
                        }
                        default_scope += split1[1];
                    }
                }
            }
            if (!b) {
                return "fail: default_scope不能为空";
            }
            client.setDefaultScope(default_scope);
        } catch (Exception e) {
            logger.error("Oauth Client 解析数据出错",e);
            status = "fail: 解析数据出错";
        }

        return status;
    }
}
