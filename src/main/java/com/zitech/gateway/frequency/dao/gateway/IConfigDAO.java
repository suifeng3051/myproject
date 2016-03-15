package com.zitech.gateway.frequency.dao.gateway;

import com.zitech.gateway.frequency.model.CarmenConfig;
import com.zitech.platform.dao.base.func.IEntityDAO;
import org.apache.ibatis.annotations.Param;

public interface IConfigDAO extends IEntityDAO<CarmenConfig, CarmenConfig> {

    CarmenConfig getByConfigName(@Param("configName") String configName);
}