package com.zitech.gateway.oauth.dao.gateway;


import com.zitech.gateway.oauth.model.OpenOauthClients;
import com.zitech.platform.dao.base.func.IEntityDAO;

import java.util.List;

public interface IOauthClientsDAO extends IEntityDAO<OpenOauthClients, OpenOauthClients> {

    OpenOauthClients getByClientId(String clientId);

    void deleteByClientId(String clientId);

    OpenOauthClients getByUserId(Integer userId);

    List<OpenOauthClients> getAll();

    List<String> getClientIdList();
}