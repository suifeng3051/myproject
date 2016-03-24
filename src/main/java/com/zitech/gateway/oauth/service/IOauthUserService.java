package com.zitech.gateway.oauth.service;


import com.zitech.gateway.oauth.model.OauthUser;

/**
 * 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: IUserService 
 * @author yang
 * @date 2015年10月21日 上午11:11:39 
 * @Copyright (c) 2015-2020 by zitech
 */
public interface IOauthUserService {

	OauthUser getUserByUserId(OauthUser oauthUser);
}
