package com.zitech.gateway.apiconfig.service;

import com.zitech.gateway.apiconfig.model.Api;

import java.util.List;

/**
 * Created by hy on 16-4-26.
 */
public interface ApiService {
    Api getApiByid(Integer id);

    void updateApi(Api api);

    void insertApi(Api api);

    void deleteApiById(Integer id);

    void deleteApiRealById(Integer id);

    boolean checkApi(String namespace, String method, String version);
    List<Api> getAll();

    List<Api> getbyGroupId(Integer group);
}
