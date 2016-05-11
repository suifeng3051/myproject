package com.zitech.gateway.param;

import com.zitech.gateway.common.ParamException;

public class BoolValidator implements IValidator {

    @Override
    public boolean v(Object object, Param param) {
        if (!param.getRequired()) {
            if (!(object == null || (object instanceof Boolean)))
                throw new ParamException(Constants.Code.PARAM_ERROR,
                        param.getName() + " should be a bool or null");
        } else {
            if (object == null || !(object instanceof Boolean))
                throw new ParamException(Constants.Code.PARAM_ERROR,
                        param.getName() + " should be a bool and not null");
        }
        return true;
    }
}
