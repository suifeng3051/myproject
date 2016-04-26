package com.zitech.gateway.param;

public class IntValidator implements IValidator {

    @Override
    public boolean v(Object object, Param param) {
        return object instanceof Integer;
    }
}
