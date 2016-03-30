package com.zitech.gateway.oauth.service.impl;


import com.zitech.gateway.oauth.dao.gateway.IOauthAccessTokensDAO;
import com.zitech.gateway.oauth.model.OpenOauthAccessTokens;
import com.zitech.gateway.oauth.service.IOpenOauthAccessTokensService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpenOauthAccessTokensService extends BaseService implements IOpenOauthAccessTokensService {

    @Autowired
    private IOauthAccessTokensDAO openOauthAccessTokensDAO;

    public IOauthAccessTokensDAO getOpenOauthAccessTokensDAO() {
        return openOauthAccessTokensDAO;
    }

    public void setOpenOauthAccessTokensDAO(IOauthAccessTokensDAO openOauthAccessTokensDAO) {
        this.openOauthAccessTokensDAO = openOauthAccessTokensDAO;
    }

    public OpenOauthAccessTokens getById(Integer id) {
        return openOauthAccessTokensDAO.getById(id);
    }

    public OpenOauthAccessTokens getByToken(String token) {
        return openOauthAccessTokensDAO.getByToken(token);
    }

    public List<OpenOauthAccessTokens> getValidToken() {
        return openOauthAccessTokensDAO.getValidToken();
    }

    @Override
    public List<OpenOauthAccessTokens> getByClientIdAndUserId(String clientId, int userId) {
        return openOauthAccessTokensDAO.getByClientIdAndUserId(clientId, userId);
    }

    public void deleteById(Integer id) {
        openOauthAccessTokensDAO.deleteById(id);
    }

    @Override
    public void deleteByIds(List<Integer> list) {
        openOauthAccessTokensDAO.deleteByIds(list);
    }

    @Override
    public void deleteByToken(String token) {
        openOauthAccessTokensDAO.deleteByToken(token);
    }

    public int save(OpenOauthAccessTokens openOauthAccessTokens) {
        openOauthAccessTokensDAO.save(openOauthAccessTokens);
        return openOauthAccessTokens.getId();
    }

    public void update(OpenOauthAccessTokens openOauthAccessTokens) {
        openOauthAccessTokensDAO.update(openOauthAccessTokens);
    }
}
