package com.zitech.gateway.apiconfig.extension;

/**
 * Created by bobo on 4/12/16.
 */
public enum ParamType {

    OUTER(1),
    INNER(2),
    FREE(3);

    private int dataFrom;

    ParamType(int dataFrom) {
        this.dataFrom = dataFrom;
    }

    public static ParamType fromValue(int dataFrom) {
        for (ParamType paramType : ParamType.values()) {
            if (paramType.dataFrom == dataFrom) {
                return paramType;
            }
        }
        throw new IllegalArgumentException("Cannot create evalue from value: " + dataFrom + "!");
    }

    public int getDataFrom() {
        return dataFrom;
    }
}
