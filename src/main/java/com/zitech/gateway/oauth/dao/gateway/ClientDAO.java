package com.zitech.gateway.oauth.dao.gateway;


import com.zitech.gateway.oauth.model.Client;

import java.util.List;

public interface ClientDAO {

    int save(Client client);

    Client getByClientId(String clientId);

    Client getById(Integer id);

    Client getByUserId(Integer userId);

    List<Client> getAll();

    List<String> getClientIdList();

    void deleteByClientId(String clientId);

    void deleteById(Integer id);

    void update(Client client);
}