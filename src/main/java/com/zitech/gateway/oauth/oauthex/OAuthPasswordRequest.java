package com.zitech.gateway.oauth.oauthex;

import org.apache.oltu.oauth2.as.request.OAuthRequest;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.validators.OAuthValidator;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by bobo on 6/8/15.
 */
public class OAuthPasswordRequest extends OAuthRequest {

    public OAuthPasswordRequest(HttpServletRequest request) throws OAuthSystemException, OAuthProblemException {
        super(request);
    }

    @Override
    protected OAuthValidator<HttpServletRequest> initValidator() throws OAuthProblemException, OAuthSystemException {
        //Integer type = Integer.valueOf(this.request.getParameter("type"));
        return new PasswordValidatorEx();
    }

    public String getUserName() {
        return getParam("username");
    }

    public String getPassword() {
        return getParam("password");
    }

    public String getGrantType() {
        return getParam(OAuth.OAUTH_GRANT_TYPE);
    }

    public String getType(){return this.request.getParameter("type");}

}