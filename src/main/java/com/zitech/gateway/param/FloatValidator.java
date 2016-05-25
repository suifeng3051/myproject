package com.zitech.gateway.param;

import com.zitech.gateway.common.ParamException;

import java.math.BigDecimal;

public class FloatValidator implements IValidator {

    @Override
    public boolean v(Object object, Param param) {
        if (!param.getRequired()) {
            if (!(object == null || (object instanceof Float
                    || object instanceof Double || object instanceof BigDecimal)))
                throw new ParamException(Constants.Code.PARAM_ERROR,
                        param.getName() + " should be a float or null");
        } else {
            if (object == null || !(object instanceof Float ||
                    object instanceof Double || object instanceof BigDecimal))
                throw new ParamException(Constants.Code.PARAM_ERROR,
                        param.getName() + " should be a float and not null");
        }
        return true;
    }
}
