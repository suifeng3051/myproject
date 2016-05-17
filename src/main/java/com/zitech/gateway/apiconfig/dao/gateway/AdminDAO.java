package com.zitech.gateway.apiconfig.dao.gateway;

import com.zitech.gateway.apiconfig.model.Admin;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AdminDAO {

    List<Admin> selectAll();

    List<Admin> selectByUserName(@Param("username") String username);

    void updatePwd(Admin user);

    int deleteByPrimaryKey(Long id);

    int insert(Admin record);

    int insertSelective(Admin record);

    Admin selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Admin record);

    int updateByPrimaryKey(Admin record);

    int getUserGroupByName(String userName);
}
