package com.zitech.gateway.apiconfig.extension;

/**
 * Created by bobo on 4/12/16.
 */
public enum ParamType {

    OUTER(1),
    INNER(2),
    FREE(3);

    private int value;

    ParamType(int value) {
        this.value = value;
    }

    public static ParamType fromValue(int dataFrom) {
        for (ParamType paramType : ParamType.values()) {
            if (paramType.value == dataFrom) {
                return paramType;
            }
        }
        throw new IllegalArgumentException("Cannot create evalue from value: " + dataFrom + "!");
    }

    public ParamType valueOf(int value) {
        return fromValue(value);
    }

    public int getValue() {
        return value;
    }
}
