package com.zitech.gateway.param;

import com.alibaba.fastjson.JSONObject;
import com.zitech.gateway.common.ParamException;

public class ObjectValidator implements IValidator {

    @Override
    public boolean v(Object object, Param param) {
        if (!param.getRequired()) {
            if (object == null || (object instanceof JSONObject))
                throw new ParamException(Constants.Code.PARAM_ERROR,
                        param.getName() + " should be an object or null");
        } else {
            if (object == null || !(object instanceof JSONObject))
                throw new ParamException(Constants.Code.PARAM_ERROR,
                        param.getName() + " should be an object and not null");
        }
        return true;
    }
}
