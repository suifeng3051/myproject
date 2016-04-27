package com.zitech.gateway.apiconfig.service.imp;

import com.alibaba.fastjson.JSON;
import com.zitech.gateway.apiconfig.dao.gateway.ApiDAO;
import com.zitech.gateway.apiconfig.dao.gateway.ParamDAO;
import com.zitech.gateway.apiconfig.dao.gateway.ServeDAO;
import com.zitech.gateway.apiconfig.model.Api;
import com.zitech.gateway.apiconfig.model.Param;
import com.zitech.gateway.apiconfig.model.Serve;
import com.zitech.gateway.apiconfig.service.ApiService;

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
    public boolean saveResult(String apiObj,String serviceObj,String paramObj,Byte env){

        Api apiModel;
        Serve serveModel;
        Param paramModel;

            try {
                apiModel = JSON.parseObject(apiObj, Api.class);
                apiModel.setEnv(env);
                apiModel.setAvail(1);
                //apiModel.setUpdatedId();
            } catch (Exception e) {
                return false;
            }

            if (checkApi(apiModel.getNamespace(), apiModel.getMethod(), apiModel.getVersion(), env) || apiModel.getId() != -1) {
                //exists update
                apiDAO.updateByPrimaryKeySelective(apiModel);
            } else {
                //add
                apiDAO.insertSelective(apiModel);
                apiModel = getApi(apiModel.getNamespace(), apiModel.getMethod(), apiModel.getVersion(), env);
            }

            try {
                serveModel = JSON.parseObject(apiObj, Serve.class);
                serveModel.setApiId(apiModel.getId());
                serveModel.setEnv(env);
            } catch (Exception e) {
                return false;
            }

            if (serveModel.getId() != -1) {
                //exists update
                serveDAO.updateByPrimaryKey(serveModel);
            } else {
                //add
                serveDAO.insertSelective(serveModel);
            }

            try {
                paramModel = JSON.parseObject(apiObj, Param.class);
                paramModel.setApiId(apiModel.getId());
                paramModel.setEnv(env);
            } catch (Exception e) {
                return false;
            }

            if (paramModel.getId() != -1) {
                //exists update
                paramDAO.updateByPrimaryKey(paramModel);
            } else {
                //add
                paramDAO.insertSelective(paramModel);
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
