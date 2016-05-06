package com.zitech.gateway.gateway;

import com.zitech.gateway.AppConfig;
import com.zitech.gateway.gateway.model.RequestEvent;
import com.zitech.gateway.utils.SpringContext;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;

import java.util.Stack;

import javax.servlet.http.HttpServletRequest;

public class PipeHelper {

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
        if (!appConfig.isPrdMode()) {
            httpHeaders.add(Constants.PARAMS_EVENT_ID, event.uuid.toString());
        }
        return httpHeaders;
    }

    public static String compactJson(String json) {
        if (StringUtils.isEmpty(json))
            return "";

        char[] charArray = json.toCharArray();
        StringBuilder sb = new StringBuilder();
        Stack<Integer> stack = new Stack<>();

        int i = 0;
        char c;
        while (i < charArray.length) {
            c = charArray[i];
            switch (c) {
                case '"':
                case '\'':
                case '\\':
                    stack.push(1);
                    sb.append(c);
                    break;
                case ' ':
                case '\t':
                case '\r':
                case '\n':
                    if (stack.size() % 2 == 1)
                        sb.append(c);
                    break;
                default: {
                    sb.append(c);
                }
            }
            ++i;
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        String s = "sadfsadf\" adfsadfsd\\ adsfsadfsdf";
        s = compactJson(s);
        System.out.println(s);
    }
}
