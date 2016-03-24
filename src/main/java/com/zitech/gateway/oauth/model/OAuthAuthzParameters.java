package com.zitech.gateway.oauth.model;

import com.zitech.gateway.oauth.LoginType;
import com.zitech.gateway.oauth.oauthex.OAuthClientCredentialRequest;
import com.zitech.gateway.oauth.oauthex.OAuthPasswordRequest;
import com.zitech.gateway.utils.LoginType2EnumUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;

import java.io.Serializable;

/**
 * Created by bobo on 6/4/15.
 */

public class OAuthAuthzParameters implements Serializable {

    private String responseType;
    private String clientId;
    private String clientSecret;
    private String userName;
    private String loginName;
    private String loginPhone;
    private String loginMail;
    private String password;
    private Integer userId;
    private String redirectUri;
    private String scope;
    private String state;
    private String authorizeCode;
    private LoginType type;

    public LoginType getType() {
        return type;
    }

    public void setType(LoginType type) {
        this.type = type;
    }



    public OAuthAuthzParameters() {

    }

    public OAuthAuthzParameters(OAuthAuthzRequest oAuthAuthzRequest) {
        this.responseType = oAuthAuthzRequest.getResponseType();
        this.clientId = oAuthAuthzRequest.getClientId();
        this.redirectUri = oAuthAuthzRequest.getRedirectURI();
        this.scope = OAuthUtils.encodeScopes(oAuthAuthzRequest.getScopes());
        this.state = oAuthAuthzRequest.getState();

        userId = 0;
    }

    public OAuthAuthzParameters(OAuthPasswordRequest oAuthPasswordRequest) {
        this.clientId = oAuthPasswordRequest.getClientId();
        this.clientSecret = oAuthPasswordRequest.getClientSecret();
        this.password = oAuthPasswordRequest.getPassword();
        this.userName = oAuthPasswordRequest.getUserName();
        if(!StringUtils.isBlank(this.userName))
        {
            if(this.userName.contains("@"))
            {
                this.loginMail = this.userName;
            }
            else if(StringUtils.isNumeric(this.userName)&&this.userName.startsWith("1"))
            {
                this.loginPhone = this.userName;
            }
            else
            {
                this.loginName = this.userName;
            }
        }

        this.type = LoginType2EnumUtils.getLoginTypeByStr(oAuthPasswordRequest.getType());

        //this.kdtId = oAuthPasswordRequest.getKdtId();
        this.scope = OAuthUtils.encodeScopes(oAuthPasswordRequest.getScopes());

        userId = 0;
    }

    public OAuthAuthzParameters(OAuthTokenRequest oAuthTokenRequest) {
        this.clientId = oAuthTokenRequest.getClientId();
        this.clientSecret = oAuthTokenRequest.getClientSecret();
        this.redirectUri = oAuthTokenRequest.getRedirectURI();
        this.scope = OAuthUtils.encodeScopes(oAuthTokenRequest.getScopes());

        userId = 0;
    }

    public OAuthAuthzParameters(OAuthClientCredentialRequest oAuthClientCredentialRequest) {
        this.clientId = oAuthClientCredentialRequest.getClientId();
        this.clientSecret = oAuthClientCredentialRequest.getClientSecret();
        this.redirectUri = oAuthClientCredentialRequest.getRedirectURI();
        this.scope = OAuthUtils.encodeScopes(oAuthClientCredentialRequest.getScopes());

        userId = 0;
    }

    public String getAuthorizeCode() {
        return authorizeCode;
    }

    public void setAuthorizeCode(String authorizeCode) {
        this.authorizeCode = authorizeCode;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLoginPhone() {
        return loginPhone;
    }

    public void setLoginPhone(String loginPhone) {
        this.loginPhone = loginPhone;
    }

    public String getLoginMail() {
        return loginMail;
    }

    public void setLoginMail(String loginMail) {
        this.loginMail = loginMail;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scopeStr) {
        this.scope = scopeStr;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isValid() {
        if (responseType == null || clientId == null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OAuthAuthzParameters that = (OAuthAuthzParameters) o;

        if (responseType != null ? !responseType.equals(that.responseType) : that.responseType != null)
            return false;
        if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null)
            return false;
        if (clientSecret != null ? !clientSecret.equals(that.clientSecret) : that.clientSecret != null)
            return false;
        if (loginMail != null ? !loginMail.equals(that.loginMail) : that.loginMail != null)
            return false;
        if (loginName != null ? !loginName.equals(that.loginName) : that.loginName != null)
            return false;
        if (loginPhone != null ? !loginPhone.equals(that.loginPhone) : that.loginPhone != null)
            return false;
        if (password != null ? !password.equals(that.password) : that.password != null)
            return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (redirectUri != null ? !redirectUri.equals(that.redirectUri) : that.redirectUri != null)
            return false;
        if (scope != null ? !scope.equals(that.scope) : that.scope != null) return false;
        if (state != null ? !state.equals(that.state) : that.state != null) return false;
        return !(authorizeCode != null ? !authorizeCode.equals(that.authorizeCode) : that.authorizeCode != null);

    }

    @Override
    public int hashCode() {
        int result = responseType != null ? responseType.hashCode() : 0;
        result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
        result = 31 * result + (clientSecret != null ? clientSecret.hashCode() : 0);
        result = 31 * result + (loginName != null ? loginName.hashCode() : 0);
        result = 31 * result + (loginPhone != null ? loginPhone.hashCode() : 0);
        result = 31 * result + (loginMail != null ? loginMail.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (redirectUri != null ? redirectUri.hashCode() : 0);
        result = 31 * result + (scope != null ? scope.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (authorizeCode != null ? authorizeCode.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OAuthAuthzParameters{" +
                "responseType='" + responseType + '\'' +
                ", clientId='" + clientId + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", loginName='" + loginName + '\'' +
                ", loginPhone='" + loginPhone + '\'' +
                ", loginMail='" + loginMail + '\'' +
                ", password='" + password + '\'' +
                ", userId=" + userId +
                ", redirectUri='" + redirectUri + '\'' +
                ", scope='" + scope + '\'' +
                ", state='" + state + '\'' +
                ", authorizeCode='" + authorizeCode + '\'' +
                '}';
    }
}
