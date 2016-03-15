package com.zitech.gateway.apiconfig.dao.gateway;

import com.zitech.gateway.apiconfig.model.CarmenServiceMethodParam;
import com.zitech.platform.dao.base.func.IEntityDAO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IServiceMethodParamDAO
        extends IEntityDAO<CarmenServiceMethodParam, CarmenServiceMethodParam> {

    /**
     *
     * @param serviceMethodId
     * @param env
     * @return
     */
    List<CarmenServiceMethodParam> getByServiceMethodId(
            @Param("serviceMethodId") long serviceMethodId, @Param("env") byte env);

    void batchSave(@Param("list") List<CarmenServiceMethodParam> list);

    /**
     * 根据主键物理删除
     * @param id 主键
     */
    void physicalDelete(@Param("id") long id);

}