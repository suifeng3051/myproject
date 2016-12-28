package com.zitech.gateway.gateway.exception;


import com.zitech.gateway.common.BaseException;

public class TokenException extends BaseException {

    public TokenException(int code, String error) {
        super(code, error);
    }

    /*
        use this method to format message
     */
    public String getMessage() {
        return super.getMessage();
    }

}
