package com.zitech.gateway.param;

import com.zitech.gateway.common.ParamException;

public class StringValidator implements IValidator {

    @Override
    public boolean v(Object object, Param param) {
        if (!param.getRequired()) {
            if (object == null || (object instanceof String))
                throw new ParamException(Constants.Code.PARAM_ERROR,
                        param.getName() + " should be a string or null");
        } else {
            if (object == null || !(object instanceof String))
                throw new ParamException(Constants.Code.PARAM_ERROR,
                        param.getName() + " should be a string and not empty");
        }
        return true;
    }
}
