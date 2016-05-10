package com.zitech.gateway.oauth.dao.user;

import com.zitech.gateway.oauth.model.Account;

public interface AccountDAO {

    Account getUserByUserId(Account oauthUser);

    Account getUserByName(String name);

    Account getUserByMobile(String mobile);

    Account getUserByMail(String mail);
}