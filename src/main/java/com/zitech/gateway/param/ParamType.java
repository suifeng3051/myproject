package com.zitech.gateway.param;


import com.zitech.gateway.exception.LogicalException;

public enum ParamType {
    ROOT,
    INT,
    STRING,
    OBJECT,
    ARRAY;

    public static ParamType from(String type) {
        ParamType[] values = ParamType.values();
        for (ParamType t : values) {
            if (t.name().equals(type)) {
                return t;
            }
        }

        throw new LogicalException(1, "unknown param type");
    }

}
