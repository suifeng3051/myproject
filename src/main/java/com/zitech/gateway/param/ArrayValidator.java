package com.zitech.gateway.param;

import com.alibaba.fastjson.JSONArray;
import com.zitech.gateway.common.ParamException;

public class ArrayValidator implements IValidator {

    @Override
    public boolean v(Object object, Param param) {
        if (!param.getRequired()) {
            if (!(object == null || (object instanceof JSONArray)))
                throw new ParamException(Constants.Code.PARAM_ERROR,
                        param.getName() + " should be an array or null");
        } else {
            if (object == null || !(object instanceof JSONArray) || ((JSONArray) object).size() == 0)
                throw new ParamException(Constants.Code.PARAM_ERROR,
                        param.getName() + " should be an array and not empty");
        }
        return true;
    }
}
