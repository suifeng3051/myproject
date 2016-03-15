package com.zitech.gateway.gateway.exception;


import com.zitech.gateway.exception.CarmenException;

public class ResultException extends CarmenException {

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
