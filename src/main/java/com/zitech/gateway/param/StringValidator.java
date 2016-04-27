package com.zitech.gateway.param;

public class StringValidator implements IValidator {

    @Override
    public boolean v(Object object, Param param) {
        if (!param.getRequired()) {
            if (object == null || (object instanceof String))
                return true;
        } else {
            if (object == null || !(object instanceof String))
                return false;
            else
                return true;
        }

        return false;
    }
}
