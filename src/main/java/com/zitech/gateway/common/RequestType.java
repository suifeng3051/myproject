package com.zitech.gateway.common;

public enum RequestType {

    POST,
    GET;

    public static RequestType from(String name) {
        RequestType[] values = RequestType.values();
        for (RequestType type : values) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }

        throw new ParamException(1, "unknown request type");
    }
}
