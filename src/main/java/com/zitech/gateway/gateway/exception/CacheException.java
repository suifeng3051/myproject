package com.zitech.gateway.gateway.exception;


import com.zitech.gateway.common.BaseException;

public class CacheException extends BaseException {

    public CacheException(int code, String error) {
        super(code, error);
    }

    /*
        use this method to format message
     */
    public String getMessage() {
        return super.getMessage();
    }

}
