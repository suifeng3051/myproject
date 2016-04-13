package com.zitech.gateway.apiconfig.extension;

/**
 * Created by bobo on 4/12/16.
 */
public enum ParamType {

    OUTER,
    INNER,
    FREE;

    public static ParamType fromValue(String value) {
        for (ParamType paramType : ParamType.values()) {
            if (paramType.name().equals(value)) {
                return paramType;
            }
        }
        throw new IllegalArgumentException("Cannot create evalue from value: " + value + "!");
    }
}
