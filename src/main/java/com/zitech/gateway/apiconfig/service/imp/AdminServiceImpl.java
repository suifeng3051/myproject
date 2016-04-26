package com.zitech.gateway.apiconfig.service.imp;


import com.zitech.gateway.apiconfig.dao.gateway.AdminDAO;
import com.zitech.gateway.apiconfig.model.Admin;
import com.zitech.gateway.apiconfig.service.AdminService;

import com.zitech.gateway.cache.RedisOperate;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by chenyun on 15/8/27.
 */
@Service
public class AdminServiceImpl implements AdminService {

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Resource
    private AdminDAO adminDAO;

    @Autowired
    private RedisOperate redisOperate;

    @Override
    public int insert(Admin user) {
        adminDAO.insert(user);
        return user.getId();
    }

    @Override
    public void update(Admin user) {
        adminDAO.updateByPrimaryKey(user);
    }

    @Override
    public void deleteById(int id) {
        adminDAO.deleteByPrimaryKey(id);
    }

    @Override
    public Admin getById(int id) {
        return adminDAO.selectByPrimaryKey(id);
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

    @Override
    public boolean isAdmin(String username) {
        try {
            List<Admin> admins = adminDAO.selectByUserName(username);
            for(Admin admin:admins)
            {
                if(admin.getUserGroup() == 1)
                {
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("判断是否为管理员出错,username:{}",username,e);
        }
        return false;
    }

    @Override
    public String getUserNameFromSessionAndRedis(HttpServletRequest request) {
        String userName = null;
        try {
            String userKey = request.getSession().getAttribute("username").toString();
            userName = redisOperate.getStringByKey(userKey);
            redisOperate.set("username", userName, 60*60); // 一小时
        } catch (Exception e) {
            logger.warn("fail to get session", e);
        }

        return userName;
    }
}
