package com.zitech.gateway.oauth.service.impl;


import com.zitech.gateway.oauth.dao.gateway.AccessTokenDAO;
import com.zitech.gateway.oauth.model.AccessToken;
import com.zitech.gateway.oauth.service.AccessTokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccessTokenServiceImpl extends BaseService implements AccessTokenService {

    @Autowired
    private AccessTokenDAO accessTokenDAO;

    public AccessTokenDAO getAccessTokenDAO() {
        return accessTokenDAO;
    }

    public void setAccessTokenDAO(AccessTokenDAO accessTokenDAO) {
        this.accessTokenDAO = accessTokenDAO;
    }

    public AccessToken getById(Integer id) {
        return accessTokenDAO.getById(id);
    }

    public AccessToken getByToken(String token) {
        return accessTokenDAO.getByToken(token);
    }

    public List<AccessToken> getValidToken() {
        return accessTokenDAO.getValidToken();
    }

    @Override
    public List<AccessToken> getByClientIdAndUserId(String clientId, int userId) {
        return accessTokenDAO.getByClientIdAndUserId(clientId, userId);
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
        accessTokenDAO.save(openOauthAccessTokens);
        return openOauthAccessTokens.getId();
    }

    public void update(AccessToken openOauthAccessTokens) {
        accessTokenDAO.update(openOauthAccessTokens);
    }
}
