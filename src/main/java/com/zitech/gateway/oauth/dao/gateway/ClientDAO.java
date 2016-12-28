package com.zitech.gateway.oauth.dao.gateway;


import com.zitech.gateway.oauth.model.Client;

import java.util.List;

public interface ClientDAO {

    int insert(Client client);

    Client selectByClientId(String clientId);

    Client selectByPrimaryKey(Integer id);

    Client selectByUserId(Integer userId);

    List<Client> selectAll();

    List<String> selectClientIds();

    void deleteByClientId(String clientId);

    void deleteById(Integer id);

    void update(Client client);
}