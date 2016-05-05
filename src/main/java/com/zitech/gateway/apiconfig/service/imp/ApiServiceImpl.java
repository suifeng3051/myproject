package com.zitech.gateway.apiconfig.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.CalendarCodec;
import com.zitech.gateway.apiconfig.dao.gateway.ApiDAO;
import com.zitech.gateway.apiconfig.dao.gateway.ParamDAO;
import com.zitech.gateway.apiconfig.dao.gateway.ServeDAO;
import com.zitech.gateway.apiconfig.model.Api;
import com.zitech.gateway.apiconfig.model.Group;
import com.zitech.gateway.apiconfig.model.Param;
import com.zitech.gateway.apiconfig.model.Serve;
import com.zitech.gateway.apiconfig.service.ApiService;

import com.zitech.gateway.apiconfig.service.GroupService;
import com.zitech.gateway.utils.AppUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

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
    @Autowired
    private GroupService groupService;


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
    public void deleteApiById(Integer id, Integer userid) {
        Api api = apiDAO.selectByPrimaryKey(id);
        api.setDeleted((byte)1);
        api.setUpdatedId(userid);
        api.setUpdatedTime(new Date());
        apiDAO.updateByPrimaryKeySelective(api);
    }

    @Override
    public void deleteApiRealById(Integer id) {
        apiDAO.deleteByPrimaryKey(id);
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

        Date now = Calendar.getInstance().getTime();

            try {
                apiModel = JSON.parseObject(apiObj, Api.class);
                apiModel.setEnv(env);
                apiModel.setAvail(1);
                apiModel.setUpdatedId(0);
                apiModel.setDeleted((byte) 0);
                apiModel.setUpdatedTime(now);
            } catch (Exception e) {
                return false;
            }

            if (apiModel.getId() != -1 || !checkApi(apiModel.getNamespace(), apiModel.getMethod(), apiModel.getVersion(), env) ) {
                //exists update
                apiDAO.updateByPrimaryKeySelective(apiModel);
            } else {
                //add
                apiModel.setId(null);
                apiDAO.insertSelective(apiModel);
                apiModel = getApi(apiModel.getNamespace(), apiModel.getMethod(), apiModel.getVersion(), env);
            }

            try {
                serveModel = JSON.parseObject(serviceObj, Serve.class);
                serveModel.setApiId(apiModel.getId());
                serveModel.setEnv(env);
                serveModel.setUpdatedId(0);
                serveModel.setDeleted((byte) 0);
                serveModel.setUpdatedTime(now);
            } catch (Exception e) {
                return false;
            }

            if (serveModel.getId() != -1) {
                //exists update
                serveDAO.updateByPrimaryKey(serveModel);
            } else {
                //add
                serveModel.setId(null);
                serveModel.setCreatedTime(now);
                serveDAO.insertSelective(serveModel);
            }

        if(StringUtils.isNotBlank(paramObj)&&!"null".equals(paramObj.trim())) {

            try {
                paramModel = JSON.parseObject(paramObj, Param.class);
                paramModel.setApiId(apiModel.getId());
                paramModel.setEnv(env);
                paramModel.setUpdatedId(0);
                paramModel.setDeleted((byte) 0);
                paramModel.setUpdatedTime(now);
            } catch (Exception e) {
                return false;
            }

            if (paramModel.getId() != -1) {
                //exists update
                paramDAO.updateByPrimaryKey(paramModel);
             } else {
                //add
                paramModel.setCreatedTime(now);
                paramModel.setId(null);
                paramDAO.insertSelective(paramModel);
            }
        }


        return true;
    }

    @Override
    public boolean uptAvail(int apiId, int avail,Byte env) {

        Api apiModel = new Api();
        apiModel.setId(apiId);
        apiModel.setAvail(avail);
        apiModel.setEnv(env);
        try{
            apiDAO.updateByPrimaryKeySelective(apiModel);
        }catch (Exception e){
            return false;
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

    @Override
    public List<Api> getAllByGroupIdAndEnv(Integer groupId, Byte env) {

        List<Api> list_api = new ArrayList<>();

        List<Group> list_group = groupService.getAllById(groupId);

        for(Group group:list_group){

            List<Api> list_singleGroup = getByGroupIdAndEnv(group.getId(),env);
            list_api.addAll(list_singleGroup);

        }


        return list_api;
    }

    @Override
    public List<Api> getDeletedApi() {
        return apiDAO.getDeletedApi();
    }
}
