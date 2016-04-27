package com.zitech.gateway.param;

import com.zitech.gateway.common.ParamException;

public class IntValidator implements IValidator {

    @Override
    public boolean v(Object object, Param param) {
        if (!param.getRequired()) {
            if (object == null || (object instanceof Integer))
                throw new ParamException(Constants.Code.PARAM_ERROR,
                        param.getName() + " should be a int or null");
        } else {
            if (object == null || !(object instanceof Integer))
                throw new ParamException(Constants.Code.PARAM_ERROR,
                        param.getName() + " should be a int and not null");
        }
        return true;
    }
}
