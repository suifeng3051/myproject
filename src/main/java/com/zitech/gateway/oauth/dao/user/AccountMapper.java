package com.zitech.gateway.oauth.dao.user;

import com.zitech.gateway.oauth.model.Account;


public interface AccountMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Account record);

    int insertSelective(Account record);

    Account selectByPrimaryKey(Long id);
    
	Account selectByUserId(Long userId);

    int updateByPrimaryKeySelective(Account record);

    int updateByPrimaryKey(Account record);

	
	int updateIntegralByUserId(Account record);

	int updateByUser(Account account);
}
