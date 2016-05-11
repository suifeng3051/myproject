package com.zitech.gateway.oauth.service;

import com.zitech.gateway.oauth.model.Client;

import java.util.List;

public interface ClientService {

    Client getById(Integer id);

    Client getByClientId(String clientId);

    Client getByUserId(Integer userId);

    void deleteById(Integer id);

    int save(Client openOauthClients);

    void update(Client openOauthClients);

    List<Client> getAll();

    List<String> getClientIdList();

}
