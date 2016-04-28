package com.zitech.gateway.gateway;


import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Constants {

    public static final String ACCESS_TOKEN = "access_token";

    public static final String ST_ALL = "st_all";
    public static final String ST_CALL = "st_call";

    public static final String ERROR_RESPONSE = "{\n\t\"code\": %d,\n\t\"message\": \"%s\",\n\t\"data\": \"\"\n}";

    public static final String PARAMS_ACCESS_TOKEN = "PARAMS-ACCESS-TOKEN";
    public static final String PARAMS_USER_ID = "PARAMS-USER-ID";
    public static final String PARAMS_CLIENT_ID = "PARAMS-CLIENT-ID";
    public static final String PARAMS_CLIENT_NAME = "PARAMS-CLIENT-NAME";
    public static final String PARAMS_REQUEST_IP = "PARAMS-REQUEST-IP";

    public static final String REQUEST_EVENT_ID = "X-REQUEST-EVENT-ID";

    public static Map<String, String> contextMap = new TreeMap<>();

    static {
        contextMap.put("Token", PARAMS_ACCESS_TOKEN);
        contextMap.put("用户ID", PARAMS_USER_ID);
        contextMap.put("客户端ID", PARAMS_CLIENT_ID);
        contextMap.put("客户端名称", PARAMS_CLIENT_NAME);
        contextMap.put("用户IP", PARAMS_REQUEST_IP);
    }
}
