package com.zitech.gateway.oauth.service.impl;


import com.zitech.gateway.cache.RedisOperate;
import com.zitech.gateway.oauth.Constants;
import com.zitech.gateway.oauth.dao.user.IOauthUserDAO;
import com.zitech.gateway.oauth.model.OauthUser;
import com.zitech.gateway.oauth.service.IOauthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: UserService 
 * @author yang
 * @date 2015年10月21日 上午11:12:28 
 * @Copyright (c) 2015-2020 by zitech
 */
@Service
public class UserService implements IOauthUserService {

    @Autowired
    private IOauthUserDAO userDAO;

    @Autowired
    private RedisOperate redis;


	public IOauthUserDAO getUserDAO() {
		return userDAO;
	}

	public void setUserDAO(IOauthUserDAO userDAO) {
		this.userDAO = userDAO;
	}


	@Override
	public OauthUser getUserByUserId(OauthUser oauthUser) {
		return userDAO.getUserByUserId(oauthUser);
	}
}
