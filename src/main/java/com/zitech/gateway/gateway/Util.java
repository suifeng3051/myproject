package com.zitech.gateway.gateway;

import com.zitech.gateway.AppConfig;
import com.zitech.gateway.gateway.model.RequestEvent;
import com.zitech.gateway.utils.SpringContext;


import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;

public class Util {

    public static final AppConfig appConfig = SpringContext.getBean(AppConfig.class);

    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.endsWith(":1")) {
            ip = "127.0.0.1";
        }
        return ip;
    }

    public static HttpHeaders getHeaders(RequestEvent event) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (appConfig.isDevMode()) {
            httpHeaders.add(Constants.REQUEST_EVENT_ID, event.uuid.toString());
        }
        return httpHeaders;
    }
}
