package com.zitech.gateway.oauth.service.impl;


import com.zitech.gateway.oauth.dao.gateway.AccessTokenDAO;
import com.zitech.gateway.oauth.model.AccessToken;
import com.zitech.gateway.oauth.service.AccessTokenService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AccessTokenServiceImpl extends BaseService implements AccessTokenService {
    public static final Logger logger = LoggerFactory
            .getLogger(AccessTokenServiceImpl.class);

    @Autowired
    private AccessTokenDAO accessTokenDAO;

    @Autowired
    protected MongoTemplate mongoTemplate;

    public AccessTokenDAO getAccessTokenDAO() {
        return accessTokenDAO;
    }

    public void setAccessTokenDAO(AccessTokenDAO accessTokenDAO) {
        this.accessTokenDAO = accessTokenDAO;
    }

    public AccessToken getById(Integer id) {
        return accessTokenDAO.selectByPrimaryKey(id);
    }

    public AccessToken getByToken(String token) {
        return accessTokenDAO.selectByToken(token);
    }

    public List<AccessToken> getValidToken() {
        return accessTokenDAO.selectValidToken();
    }

    @Override
    public List<AccessToken> getByClientIdAndUserId(String clientId, int userId) {
        return accessTokenDAO.selectByClientIdAndUserId(clientId, userId);
    }

    public void deleteById(Integer id) {
        accessTokenDAO.deleteById(id);
    }

    @Override
    public void deleteByIds(List<Integer> list) {
        accessTokenDAO.deleteByIds(list);
    }

    @Override
    public void deleteByToken(String token) {
        accessTokenDAO.deleteByToken(token);
    }

    public int save(AccessToken openOauthAccessTokens) {
        accessTokenDAO.insert(openOauthAccessTokens);
        return openOauthAccessTokens.getId();
    }

    public void update(AccessToken openOauthAccessTokens) {
        accessTokenDAO.update(openOauthAccessTokens);
    }

    @Override
    public void saveAccessTokenToMongoDb(List<AccessToken> tokensList) {
        for(AccessToken accessToken:tokensList)
        {
            Map<String, Object> map = new HashMap<>();
            map.put("id",accessToken.getId());
            map.put("access_token",accessToken.getAccessToken());
            map.put("client_id",accessToken.getClientId());
            map.put("user_id",accessToken.getUserId());
            map.put("extra",accessToken.getExtra());
            map.put("expires",accessToken.getExpires());
            map.put("scope",accessToken.getScope());
            mongoTemplate.insert(map,"access_token");
        }
    }
}
