package com.zitech.gateway.oauth.service.impl;


import com.zitech.gateway.oauth.dao.gateway.RefreshTokenDAO;
import com.zitech.gateway.oauth.model.RefreshToken;
import com.zitech.gateway.oauth.service.RefreshTokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class RefreshTokenServiceImpl extends BaseService implements RefreshTokenService {

    @Autowired
    private RefreshTokenDAO refreshTokenDAO;

    public RefreshTokenDAO getRefreshTokenDAO() {
        return refreshTokenDAO;
    }

    public void setRefreshTokenDAO(RefreshTokenDAO refreshTokenDAO) {
        this.refreshTokenDAO = refreshTokenDAO;
    }

    public RefreshToken getById(Integer id) {
        return refreshTokenDAO.selectByPrimaryKey(id);
    }

    public RefreshToken getByToken(String token) {
        return refreshTokenDAO.selectByToken(token);
    }

    @Override
    public List<RefreshToken> getByAccessTokens(List<String> list) {
        if (list.size() == 0) {
            return new LinkedList<>();
        }
        return refreshTokenDAO.selectByAccessTokens(list);
    }

    public void deleteById(Integer id) {
        refreshTokenDAO.deleteById(id);
    }

    @Override
    public void deleteByToken(String token) {
        refreshTokenDAO.deleteByToken(token);
    }

    @Override
    public void deleteByAccessTokens(List<String> list) {
        refreshTokenDAO.deleteByAccessTokens(list);
    }

    public int save(RefreshToken openOauthRefreshTokens) {
        refreshTokenDAO.insert(openOauthRefreshTokens);
        return openOauthRefreshTokens.getId();
    }

    public void update(RefreshToken openOauthRefreshTokens) {
        refreshTokenDAO.update(openOauthRefreshTokens);
    }
}
