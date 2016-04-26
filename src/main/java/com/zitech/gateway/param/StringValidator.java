package com.zitech.gateway.param;

public class StringValidator implements IValidator {

    @Override
    public boolean v(Object object, Param param) {
        return object instanceof String;
    }
}
