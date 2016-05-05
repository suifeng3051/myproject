package com.zitech.gateway.apiconfig.service;

import com.zitech.gateway.apiconfig.model.Api;

import java.util.List;

/**
 * Created by hy on 16-4-26.
 */
public interface ApiService {
    Api getApiById(Integer id);

    void updateApi(Api api);

    void insertApi(Api api);

    void deleteApiById(Integer id, Integer userid);

    void deleteApiRealById(Integer id);

    /** add by pxl
     * 验证该API是否存在
     * @param namespace
     * @param method
     * @param version
     * @param env
     * @return  不存在就是true 存在就是false
     */
    boolean checkApi(String namespace, String method, Integer version, Byte env);

    /** add by pxl
     * 获取API实体类
     * @param namespace
     * @param method
     * @param version
     * @param env
     * @return
     */
    Api getApi(String namespace,String method,Integer version,Byte env);


    /**
     * 保存新增或修改API的结果
     * @param apiObj
     * @param serviceObj
     * @param paramObj
     * @param env
     * @return
     */
    boolean  saveResult(String apiObj,String serviceObj,String paramObj,Byte env);


    boolean uptAvail(int apiId,int avail,Byte env);


    List<Api> getAllByEnv(Byte env);

    /**
     * 获取指定group内的API
     * @param groupId
     * @param env
     * @return
     */
    List<Api> getByGroupIdAndEnv(Integer groupId, Byte env);


    /**
     * 获取指定group自身及其下级的所有API
     * @param groupId
     * @param env
     * @return
     */
    List<Api> getAllByGroupIdAndEnv(Integer groupId,Byte env);



    List<Api> getDeletedApi();
}
