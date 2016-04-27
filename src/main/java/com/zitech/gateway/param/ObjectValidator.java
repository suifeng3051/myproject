package com.zitech.gateway.param;

import com.alibaba.fastjson.JSONObject;

public class ObjectValidator implements IValidator {

    @Override
    public boolean v(Object object, Param param) {
        return object instanceof JSONObject;
    }
}
