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
        httpHeaders.add(Constants.PARAMS_EVENT_ID, event.uuid.toString());
        return httpHeaders;
    }

    /**
     * compact json string, remove unnecessary ' ' and '\n' and '\r'
     * @param json valid json string
     * @return compact json string
     */
    public static String compactJson(String json) {
        if (StringUtils.isEmpty(json))
            return "";

        char[] charArray = json.toCharArray();
        StringBuilder sb = new StringBuilder();

        int i = 0, count = 0, slash = 0;
        char c, b = 0;
        while (i < charArray.length) {
            c = charArray[i];
            switch (c) {
                case '"':
                case '\'':
                    if (count % 2 == 0) {
                        b = c;
                        ++count;
                    } else {
                        if (c == b) {
                            if (slash % 2 == 0) {
                                ++count;
                                b = 0;
                            } else {
                                ++slash; // tread \" as \\ to ++slash
                            }
                        }
                    }
                    sb.append(c);
                    break;
                case '\\':
                    ++slash;
                    sb.append(c);
                    break;
                case ' ':
                case '\t':
                case '\r':
                case '\n':
                    if (count % 2 == 1)
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
}
