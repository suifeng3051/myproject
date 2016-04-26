package com.zitech.gateway.param;

import com.alibaba.fastjson.JSONArray;

public class ArrayValidator implements IValidator {

    @Override
    public boolean v(Object object, Param param) {
        return object instanceof JSONArray;
    }
}
