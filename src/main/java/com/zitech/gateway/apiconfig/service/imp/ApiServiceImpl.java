package com.zitech.gateway.apiconfig.service.imp;

import com.zitech.gateway.apiconfig.dao.gateway.ApiDAO;
import com.zitech.gateway.apiconfig.model.Api;
import com.zitech.gateway.apiconfig.service.ApiService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public void saveResult(String parseApiResult, String apiParamUpdate, String apiParamAdd, String methodResult, String methodParamUpdate, String methodParamAdd,
                           String structureUpdate, String structureAdd, String paramMappingUpdate, String paramMappingAdd, String methodMappingId, Byte env) {

        Map<String,Object> paraMap = new HashMap<>();
        paraMap.put("parseApiResult",parseApiResult);
        paraMap.put("apiParamUpdate",apiParamUpdate);
        paraMap.put("apiParamAdd",apiParamAdd);
        paraMap.put("methodResult",methodResult);
        paraMap.put("methodParamUpdate",methodParamUpdate);
        paraMap.put("methodParamAdd",methodParamAdd);
        paraMap.put("structureUpdate",structureUpdate);
        paraMap.put("structureAdd",structureAdd);
        paraMap.put("paramMappingUpdate",paramMappingUpdate);
        paraMap.put("paramMappingAdd",paramMappingAdd);
        paraMap.put("methodMappingId",methodMappingId);
        paraMap.put("env",env);

        if(true){
            //exists update

        }else{
            //add


        }


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
