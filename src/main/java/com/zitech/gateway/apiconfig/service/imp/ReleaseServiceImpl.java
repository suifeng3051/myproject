package com.zitech.gateway.apiconfig.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.zitech.gateway.apiconfig.dao.gateway.ApiDAO;
import com.zitech.gateway.apiconfig.dao.gateway.ParamDAO;
import com.zitech.gateway.apiconfig.dao.gateway.ServeDAO;
import com.zitech.gateway.apiconfig.model.Api;
import com.zitech.gateway.apiconfig.model.Param;
import com.zitech.gateway.apiconfig.model.Serve;
import com.zitech.gateway.apiconfig.service.ReleaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public JSONObject getDownloadInfo(String id){

        int apiId = Integer.parseInt(id);

        JSONObject jsonObject = new JSONObject();

        Api api = apiDAO.selectByPrimaryKey(apiId);
        Serve serve = serveDAO.selectByApiId(apiId);
        Param param = paramDAO.selectByApiId(apiId);

        jsonObject.put("api",JSONObject.toJSON(api));
        jsonObject.put("serve",JSONObject.toJSON(serve));
        jsonObject.put("param",JSONObject.toJSON(param));

        return jsonObject;

    }

}
