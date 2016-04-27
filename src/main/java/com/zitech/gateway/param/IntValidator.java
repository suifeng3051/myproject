package com.zitech.gateway.param;

public class IntValidator implements IValidator {

    @Override
    public boolean v(Object object, Param param) {
        if (!param.getRequired()) {
            if (object == null || (object instanceof Integer))
                return true;
        } else {
            if (object == null || !(object instanceof Integer))
                return false;
            else
                return true;
        }

        return false;
    }
}
