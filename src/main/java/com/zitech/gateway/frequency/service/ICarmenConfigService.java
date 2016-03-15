package com.zitech.gateway.frequency.service;

import com.zitech.gateway.frequency.model.CarmenConfig;

/**
 * Created by chenyun on 15/8/10.
 */
public interface ICarmenConfigService {

    final String prefix = "carmen_config_";

    int insert(CarmenConfig config);

    void update(CarmenConfig config);

    void deleteById(int id);

    CarmenConfig getById(int id);

    CarmenConfig getByConfigName(String configName);

}
