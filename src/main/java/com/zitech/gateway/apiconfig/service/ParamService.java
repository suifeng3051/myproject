package com.zitech.gateway.apiconfig.service;


import com.zitech.gateway.apiconfig.model.Param;
import com.zitech.gateway.apiconfig.model.Serve;

import java.util.List;

public interface ParamService {

    Param getByApiId(Integer apiId);

    List<Param> getAll();

    void deleteParamByApiId(Integer apiId, Integer userid);
}
