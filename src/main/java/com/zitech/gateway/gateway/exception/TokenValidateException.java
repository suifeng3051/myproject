package com.zitech.gateway.gateway.exception;


import com.zitech.gateway.common.BaseException;

public class TokenValidateException extends BaseException {

    public TokenValidateException(int code, String error) {
        super(code, error);
    }

    /*
        use this method to format message
     */
    public String getMessage() {
        return super.getMessage();
    }

}
