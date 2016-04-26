package com.zitech.gateway.gateway.exception;


import com.zitech.gateway.exception.BaseException;

public class ResultException extends BaseException {

    public ResultException(int code, String error) {
        super(code, error);
    }

    /*
        use this method to format message
     */
    public String getMessage() {
        return super.getMessage();
    }

}
