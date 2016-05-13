package com.zitech.gateway.apiconfig.service;


import com.zitech.gateway.apiconfig.model.Admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;


public interface AdminService {

    String prefix = "api_user_";

    int insert(Admin user);

    void update(Admin user);

    void deleteById(int id);

    Admin getById(int id);

    List<Admin> getByUserName(String username);

    List<Admin> getAllList();

    void updatePwd(Admin user);

    boolean isAdmin(String username);

    int getUserGroup(String username);

    String getUserNameFromSessionAndRedis(HttpServletRequest request);


}
