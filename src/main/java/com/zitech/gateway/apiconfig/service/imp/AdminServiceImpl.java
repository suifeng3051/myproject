package com.zitech.gateway.apiconfig.service.imp;

import com.zitech.gateway.apiconfig.dao.gateway.AdminDAO;
import com.zitech.gateway.apiconfig.model.Admin;
import com.zitech.gateway.apiconfig.service.AdminService;

import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

/**
 * Created by chenyun on 15/8/27.
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Resource
    private AdminDAO adminDAO;

    @Override
    public int insert(Admin user) {
        adminDAO.save(user);
        return user.getId();
    }

    @Override
    public void update(Admin user) {
        adminDAO.update(user);
    }

    @Override
    public void deleteById(int id) {
        adminDAO.deleteById(id);
    }

    @Override
    public Admin getById(int id) {
        return adminDAO.getById(id);
    }

    @Override
    public List<Admin> getByUserName(String username) {
        return adminDAO.selectByUserName(username);
    }

    @Override
    public List<Admin> getAllList() {
        return adminDAO.selectAll();
    }

    @Override
    public void updatePwd(Admin user) {
        adminDAO.updatePwd(user);
    }
}
