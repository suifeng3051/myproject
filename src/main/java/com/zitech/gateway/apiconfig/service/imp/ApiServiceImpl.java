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
    public Api getApiByid(Integer id) {
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
    public boolean checkApi(String namespace, String method, String version, Byte env) {

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
    public Api getApi(String namespace, String method, String version, Byte env) {
        Map<String,Object> paraMap = new HashMap<>();
        paraMap.put("nameSpace",namespace);
        paraMap.put("method",method);
        paraMap.put("version",version);
        paraMap.put("env",env);

        return  apiDAO.findApiForCheck(paraMap);

    }

    @Override
    public List<Api> getAllByEnv(Byte env) {
        return apiDAO.getAllApiByEnv(env);
    }

    @Override
    public List<Api> getbyGroupIdAndEnv(Integer groupid,Byte env) {
        return apiDAO.getByGroupIdAndEnv(groupid, env);
    }
}
