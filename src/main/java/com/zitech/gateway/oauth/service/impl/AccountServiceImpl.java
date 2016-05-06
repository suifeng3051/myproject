package com.zitech.gateway.oauth.service.impl;


import com.zitech.gateway.cache.RedisOperate;
import com.zitech.gateway.oauth.dao.user.AccountDAO;
import com.zitech.gateway.oauth.model.Account;
import com.zitech.gateway.oauth.service.AccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private RedisOperate redis;


	public AccountDAO getAccountDAO() {
		return accountDAO;
	}

	public void setAccountDAO(AccountDAO accountDAO) {
		this.accountDAO = accountDAO;
	}


	@Override
	public Account getUserByUserId(Account oauthUser) {
		return accountDAO.getUserByUserId(oauthUser);
	}
}
