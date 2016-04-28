package com.zitech.gateway.apiconfig.service.imp;

import com.alibaba.fastjson.JSON;
import com.zitech.gateway.apiconfig.dao.gateway.ApiDAO;
import com.zitech.gateway.apiconfig.dao.gateway.ParamDAO;
import com.zitech.gateway.apiconfig.dao.gateway.ServeDAO;
import com.zitech.gateway.apiconfig.model.Api;
import com.zitech.gateway.apiconfig.model.Param;
import com.zitech.gateway.apiconfig.model.Serve;
import com.zitech.gateway.apiconfig.service.ApiService;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hy on 16-4-26.
 */
@Service
public class ApiServiceImpl implements ApiService {

    @Autowired
    private ApiDAO apiDAO;
    @Autowired
    private ServeDAO serveDAO;
    @Autowired
    private ParamDAO paramDAO;


    @Override
    public Api getApiById(Integer id) {
        return apiDAO.selectByPrimaryKey(id);
    }

    @Override
    public void updateApi(Api api) {
        apiDAO.updateByPrimaryKeySelective(api);
    }

    @Override
    public void insertApi(Api api) {
        apiDAO.insertSelective(api);
    }

    @Override
    public void deleteApiById(Integer id) {

    }

    @Override
    public void deleteApiRealById(Integer id) {

    }

    @Override
    public boolean checkApi(String namespace, String method, Integer version, Byte env) {

        Map<String,Object> paraMap = new HashMap<>();
        paraMap.put("nameSpace",namespace);
        paraMap.put("method",method);
        paraMap.put("version",version);
        paraMap.put("env",env);
        Api api = apiDAO.findApiForCheck(paraMap);

        if(api==null){
            return true;
        }else{
            return false;
        }

     }

    @Override
    public Api getApi(String namespace, String method, Integer version, Byte env) {
        Map<String,Object> paraMap = new HashMap<>();
        paraMap.put("nameSpace",namespace);
        paraMap.put("method",method);
        paraMap.put("version",version);
        paraMap.put("env",env);

        return  apiDAO.findApiForCheck(paraMap);

    }

    @Transactional
    @Override
    public boolean saveResult(Api apiObj,Serve serviceObj,Param paramObj,Byte env){


        if(apiObj.getId()!=-1){

            apiDAO.updateByPrimaryKeySelective(apiObj);
        }else{

            apiDAO.insertSelective(apiObj);
        }

            if (serviceObj.getId() != -1) {
                //exists update
                serveDAO.updateByPrimaryKey(serviceObj);
            } else {
                //add
                serveDAO.insertSelective(serviceObj);
            }


        if(paramObj!=null) {
            if (paramObj.getId() != -1) {
                //exists update
                paramDAO.updateByPrimaryKey(paramObj);
            } else {
                //add
                paramDAO.insertSelective(paramObj);
            }
        }


        return true;
    }

    @Override
    public List<Api> getAllByEnv(Byte env) {
        return apiDAO.getAllApiByEnv(env);
    }

    @Override
    public List<Api> getByGroupIdAndEnv(Integer groupId, Byte env) {
        return apiDAO.getByGroupIdAndEnv(groupId, env);
    }
}
