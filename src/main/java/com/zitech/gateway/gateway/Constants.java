package com.zitech.gateway.gateway;


import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Constants {

    // for token
    public static final String ACCESS_TOKEN = "access_token";
    public static final String METHOD = "method";

    // for secret
    public static final String SIGN = "sign";
    public static final String CLIENTID = "clientid";

    public static final String ST_ALL = "st_all";
    public static final String ST_PRE = "st_pre";
    public static final String ST_CALL = "st_call";
    public static final String ST_POST = "st_post";

    public static final String ERROR_RESPONSE = "{\n\t\"code\": %d,\n\t\"message\": \"%s\",\n\t\"data\": \"\"\n}";

    public static final String CONTEXT_ACCESS_TOKEN = "access_token";
    public static final String CONTEXT_USER_ID = "user_id";
    public static final String CONTEXT_REQUEST_IP = "request_ip";
    public static final String CONTEXT_CLIENT_ID = "client_id";
    public static final String CONTEXT_CLIENT_NAME = "client_name";
    public static final String CONTEXT_CLIENT_NUM = "client_num";

    public static Map<String, String> contextMap = new TreeMap<>();

    static {
        contextMap.put("Token", CONTEXT_ACCESS_TOKEN);
        contextMap.put("用户ID", CONTEXT_USER_ID);
        contextMap.put("用户IP", CONTEXT_REQUEST_IP);
        contextMap.put("客户端ID", CONTEXT_CLIENT_ID);
        contextMap.put("客户端名称", CONTEXT_CLIENT_NAME);
    }
}
