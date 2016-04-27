package com.zitech.gateway.oauth.model;


import com.alibaba.fastjson.annotation.JSONField;
import com.zitech.gateway.gateway.Constants;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;

public class AccessToken implements Serializable {

    private Integer id;

    private String accessToken;

    private String clientId; // client_id in Open_Oauth_Client

    private Integer userId;

    private String extra;

    private Date expires;

    private String scope;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JSONField(name = "access_token")
    public String getAccessToken() {
        return accessToken;
    }

    @JSONField(name = "access_token")
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @JSONField(name = "client_id")
    public String getClientId() {
        return clientId;
    }

    @JSONField(name = "client_id")
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @JSONField(name = "user_id")
    public Integer getUserId() {
        return userId;
    }

    @JSONField(name = "user_id")
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    public Date getExpires() {
        return expires;
    }

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    public void setExpires(Date expires) {
        this.expires = expires;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "OpenOauthAccessTokens{" +
                "id=" + id +
                ", accessToken='" + accessToken + '\'' +
                ", clientId='" + clientId + '\'' +
                ", userId=" + userId +
                ", extra='" + extra + '\'' +
                ", expires=" + expires +
                ", scope='" + scope + '\'' +
                '}';
    }
}