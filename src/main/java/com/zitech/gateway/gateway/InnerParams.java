package com.zitech.gateway.gateway;

import com.zitech.gateway.gateway.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bobo on 4/12/16.
 */
public class InnerParams {

    /**
     * Header parameters
     */
    public static final String PARAMS_USER_ID = "PARAMS-USER-ID";
    public static final String PARAMS_CLIENT_ID = "PARAMS-CLIENT-ID";
    public static final String PARAMS_CLIENT_NAME = "PARAMS-CLIENT-NAME";
    public static final String PARAMS_REQUEST_IP = "PARAMS-REQUEST-IP";
    public static final String PARAMS_ACCESS_TOKEN = "PARAMS-ACCESS-TOKEN";

    private static Map<String, String> headNamesMap = new HashMap<String, String>();

    static {
        headNamesMap.put(Constants.CONTEXT_USER_ID, PARAMS_USER_ID);
        headNamesMap.put(Constants.CONTEXT_CLIENT_ID, PARAMS_CLIENT_ID);
        headNamesMap.put(Constants.CONTEXT_CLIENT_NAME, PARAMS_CLIENT_NAME);
        headNamesMap.put(Constants.CONTEXT_REQUEST_IP, PARAMS_REQUEST_IP);
        headNamesMap.put(Constants.CONTEXT_ACCESS_TOKEN, PARAMS_ACCESS_TOKEN);
    }

    public static String getHeadName(String paramName) {
        return headNamesMap.get(paramName);
    }
}
