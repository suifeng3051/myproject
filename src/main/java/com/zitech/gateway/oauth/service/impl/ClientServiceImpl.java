package com.zitech.gateway.oauth.service.impl;


import com.zitech.gateway.oauth.dao.gateway.ClientDAO;
import com.zitech.gateway.oauth.model.Client;
import com.zitech.gateway.oauth.service.ClientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientServiceImpl extends BaseService implements ClientService {

    @Autowired
    private ClientDAO clientDAO;

    public ClientDAO getClientDAO() {
        return clientDAO;
    }

    public void setClientDAO(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }

    public Client getById(Integer id) {
        return clientDAO.getById(id);
    }

    public Client getByClientId(String clientId) {
        return clientDAO.getByClientId(clientId);
    }

    @Override
    public Client getByUserId(Integer userId) {
        return clientDAO.getByUserId(userId);
    }

    public void deleteById(Integer id) {
        clientDAO.deleteById(id);
    }

    public void deleteByClientId(String clientId) {
        clientDAO.deleteByClientId(clientId);
    }

    public int save(Client openOauthClients) {
        clientDAO.save(openOauthClients);
        return openOauthClients.getId();
    }

    public void update(Client openOauthClients) {
        clientDAO.update(openOauthClients);
    }

    @Override
    public List<Client> getAll() {
        return clientDAO.getAll();
    }

    @Override
    public List<String> getClientIdList() {
        return clientDAO.getClientIdList();
    }
}
