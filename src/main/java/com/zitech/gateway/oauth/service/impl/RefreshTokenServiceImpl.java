package com.zitech.gateway.oauth.service.impl;


import com.zitech.gateway.oauth.dao.gateway.RefreshTokenDAO;
import com.zitech.gateway.oauth.model.RefreshToken;
import com.zitech.gateway.oauth.service.RefreshTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class RefreshTokenServiceImpl extends BaseService implements RefreshTokenService {

    public static final Logger logger = LoggerFactory
            .getLogger(RefreshTokenServiceImpl.class);


    @Autowired
    private RefreshTokenDAO refreshTokenDAO;

    @Autowired
    protected MongoTemplate mongoTemplate;

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

    @Override
    public void saveRefreshTokenToMongoDb(List<String> tkList) {

        List<RefreshToken> refreshTokens = refreshTokenDAO.selectByAccessTokens(tkList);
        for (RefreshToken refreshToken:refreshTokens) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", refreshToken.getId());
            map.put("refresh_token", refreshToken.getRefreshToken());
            map.put("client_id", refreshToken.getClientId());
            map.put("user_id", refreshToken.getUserId());
            map.put("extra", refreshToken.getExtra());
            map.put("access_token", refreshToken.getAccessToken());
            map.put("expires", refreshToken.getExpires());
            map.put("scope", refreshToken.getScope());
            mongoTemplate.insert(map, "refresh_token");
        }
    }
}
