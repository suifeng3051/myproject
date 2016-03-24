package com.zitech.gateway.oauth.dao.user;

import com.zitech.gateway.oauth.model.OauthUser;
import com.zitech.platform.dao.base.func.IEntityDAO;

public interface IOauthUserDAO extends IEntityDAO<OauthUser, OauthUser> {
    OauthUser getUserByUserId(OauthUser oauthUser);
    OauthUser getUserByName(String name);
    OauthUser getUserByMobile(String mobile);
    OauthUser getUserByMail(String mail);

}