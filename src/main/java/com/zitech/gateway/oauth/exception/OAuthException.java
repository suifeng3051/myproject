package com.zitech.gateway.oauth.exception;

import com.zitech.gateway.exception.CarmenException;

/**
 * Created by bobo on 6/11/15.
 */
public class OAuthException extends CarmenException {

    public OAuthException(int code, String error) {
        super(code, error);
    }

    /*
        use this method to format message
     */
    public String getMessage() {
        return super.getMessage();
    }

}
