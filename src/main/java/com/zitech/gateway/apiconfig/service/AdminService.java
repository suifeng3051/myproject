package com.zitech.gateway.apiconfig.service;


import com.zitech.gateway.apiconfig.model.Admin;

import java.util.List;


public interface AdminService {

    String prefix = "api_user_";

    int insert(Admin user);

    void update(Admin user);

    void deleteById(int id);

    Admin getById(int id);

    List<Admin> getByUserName(String username);

    List<Admin> getAllList();

    void updatePwd(Admin user);

}
