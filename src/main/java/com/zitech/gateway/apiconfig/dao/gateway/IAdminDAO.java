package com.zitech.gateway.apiconfig.dao.gateway;

import com.zitech.gateway.apiconfig.model.CarmenUser;
import com.zitech.platform.dao.base.func.IEntityDAO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IAdminDAO extends IEntityDAO<CarmenUser, CarmenUser> {

    List<CarmenUser> getAllList();

    List<CarmenUser> getByUserName(@Param("username") String username);
}