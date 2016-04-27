package com.zitech.gateway.param;

import com.alibaba.fastjson.JSONObject;

public class ObjectValidator implements IValidator {

    @Override
    public boolean v(Object object, Param param) {
        if (!param.getRequired()) {
            if (object == null || (object instanceof JSONObject))
                return true;
        } else {
            if (object == null || !(object instanceof JSONObject))
                return false;
            else
                return true;
        }

        return false;
    }
}
