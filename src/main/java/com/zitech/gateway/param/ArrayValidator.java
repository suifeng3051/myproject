package com.zitech.gateway.param;

import com.alibaba.fastjson.JSONArray;

public class ArrayValidator implements IValidator {

    @Override
    public boolean v(Object object, Param param) {
        if (!param.getRequired()) {
            if (object == null || (object instanceof JSONArray))
                return true;
        } else {
            if (object == null || !(object instanceof JSONArray) || ((JSONArray) object).size() == 0)
                return false;
            else
                return true;
        }

        return false;
    }
}
