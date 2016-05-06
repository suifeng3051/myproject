package com.zitech.gateway.apiconfig.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zitech.gateway.apiconfig.dao.gateway.ApiDAO;
import com.zitech.gateway.apiconfig.dao.gateway.ParamDAO;
import com.zitech.gateway.apiconfig.dao.gateway.ServeDAO;
import com.zitech.gateway.apiconfig.model.Api;
import com.zitech.gateway.apiconfig.model.Param;
import com.zitech.gateway.apiconfig.model.Serve;
import com.zitech.gateway.apiconfig.service.ApiService;
import com.zitech.gateway.apiconfig.service.ReleaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by panxl on 2016/5/4.
 */
@Service
public class ReleaseServiceImpl implements ReleaseService {

    @Autowired
    private ApiDAO apiDAO;
    @Autowired
    private ServeDAO serveDAO;
    @Autowired
    private ParamDAO paramDAO;
    @Autowired
    private ApiService apiService;

    @Override
    public JSONObject getDownloadInfo (String id,byte toEnv){

        int apiId = Integer.parseInt(id);

        JSONObject jsonObject = new JSONObject();

        Api api = apiDAO.selectByPrimaryKey(apiId);
        Serve serve = serveDAO.selectByApiId(apiId);
        Param param = paramDAO.selectByApiId(apiId);

        jsonObject.put("api",JSONObject.toJSON(api));
        jsonObject.put("serve",JSONObject.toJSON(serve));
        jsonObject.put("param",JSONObject.toJSON(param));
        jsonObject.put("toEnv",toEnv);

        return jsonObject;

    }

    @Transactional
    @Override
    public JSONObject loadUploadFile(JSONObject jsonObject) {


            Api api = JSONObject.toJavaObject(jsonObject.getJSONObject("api"),Api.class);
            Serve serve = JSONObject.toJavaObject(jsonObject.getJSONObject("serve"),Serve.class);
            Param param = JSONObject.toJavaObject(jsonObject.getJSONObject("param"),Param.class);
            byte env =   jsonObject.getByte("toEnv");

            if(apiService.checkApi(api.getNamespace(),api.getMethod(),api.getVersion(),env)){

                api.setId(apiDAO.getMaxId()+1);
                api.setEnv(env);
                serve.setId(null);
                serve.setApiId(api.getId());
                serve.setEnv(env);
                param.setId(null);
                param.setApiId(api.getId());
                param.setEnv(env);

                try{
                    apiDAO.insertSelective(api);
                    serveDAO.insertSelective(serve);
                    paramDAO.insertSelective(param);
                }catch (Exception e){
                    JSONObject result_obj = new JSONObject();
                    result_obj.put("api",api);
                    result_obj.put("code",1);
                    result_obj.put("message","入库发布失败,"+e.getMessage());
                    return result_obj;
                }

            }else{
                JSONObject result_obj = new JSONObject();
                result_obj.put("api",api);
                result_obj.put("code",1);
                result_obj.put("message","该API已存在");
                return result_obj;


            }


        return null;

    }

}