package com.zitech.gateway.oauth.service;

import com.zitech.gateway.oauth.model.OpenOauthClients;

import java.util.List;

public interface IOpenOauthClientsService {

    OpenOauthClients getById(Integer id);

    OpenOauthClients getByClientId(String clientId);

    OpenOauthClients getByUserId(Integer userId);

    void deleteById(Integer id);

    int save(OpenOauthClients openOauthClients);

    void update(OpenOauthClients openOauthClients);

    List<OpenOauthClients> getAll();

    List<String> getClientIdList();

}
