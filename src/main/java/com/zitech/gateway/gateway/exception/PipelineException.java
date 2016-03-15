package com.zitech.gateway.gateway.exception;


import com.zitech.gateway.exception.CarmenException;

public class PipelineException extends CarmenException {

    public PipelineException(int code, String error) {
        super(code, error);
    }

    /*
        use this method to format message
     */
    public String getMessage() {
        return super.getMessage();
    }

}
