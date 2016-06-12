package com.zitech.gateway.oauth.oauthex;

public final class OAuthConstants {

    // for oauth description
    public class OAuthDescription {
        public static final String INVALID_CLIENT_DESCRIPTION = "团队校验失败";
        public static final String INVALID_GRANT_TYPE = "非法授权类型";
        public static final String INVALID_REDIRECT_URI = "跳转链接不正确";
        public static final String INVALID_REDIRECT_URI_NOT_FOUND = "授权时需要提供跳转链接";
        public static final String INVALID_AUTHORIZATION_CODE = "非法授权码";
        public static final String INVALID_TOKEN_EXPIRED = "访问码已过期";
        public static final String INVALID_FRESH_TOKEN = "非法刷新码";
        public static final String INVALID_FRESH_FREQUENCY = "重复刷新访问码，请稍后再试";
        public static final String INVALID_LOGIN_FREQUENCY = "登录太频繁，请稍后再试";
        public static final String INVALID_RESOURCE = "请求非法资源";
    }

    // for oauth response
    public class OAuthResponse {
        // for token
        public static final int INVALID_CLIENT = 5101;
        public static final int INVALID_CLIENT_NO = 5102;
        public static final int INVALID_CLIENT_INFO = 5108;
        public static final int NO_OR_EXPIRED_TOKEN = 5110;
        public static final int INVALID_TOKEN = 5111;
        public static final int INVALID_FREQUENCY = 5113;
        public static final int INVALID_CODE = 5120;
        public static final int INVALID_REQUEST = 5121;
        public static final int INVALID_USER    = 5133; //用户不存在
        public static final int INVALID_PASSWORD = 5135;    //密码错误
    }
}
