package com.zitech.gateway.apiconfig.service;


import com.zitech.gateway.apiconfig.model.Serve;

import java.util.List;

public interface ServeService {

    Serve getByApiId(Integer apiId);

    List<Serve> getAll(Byte env);

    void deleteServeByApiId(Integer apiId, Integer userid);

    void updateServe(Serve serve);

    void deleteServeRealById(Integer id);
}
