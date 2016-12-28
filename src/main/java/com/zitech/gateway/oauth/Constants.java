package com.zitech.gateway.oauth;


public final class Constants {

    // for app
    public final static String APP_ENCODING = "utf-8";

    // for cache
    public final static String CACHE_TOKEN_KEY_PATTERN = "oauth_access_token_%s";
    public final static String CACHE_ACCESS_TOKEN_KEY_PATTERN = "oauth_access_token_*";
    public static final String CACHE_NAME_ACCESS_TOKEN = "oauth_access_token_name";
    public final static String CACHE_CLIENT_KEY_PATTERN = "cache_client_key_%s";
    public final static String CACHE_REFRESH_FREQUENCY_PATTERN = "cache_refresh_frequency_pattern_%s";
    public final static String CACHE_LOGIN_FREQUENCY_PATTERN = "cache_login_frequency_pattern_%s";

    // for login
    public final static String DEFAULT_CAPTCHA_PARAM = "captcha";

    // for user
    public final static String USER_ID = "user_id";
    public final static String USER_ACCOUNT = "user_account";
    public final static String CLIENT_ID = "client_id";
    public final static String CLIENT_NAME = "client_name";
    public final static String CLIENT_QUERY = "client_query";
    public static final String USER_USER_BY_ID_KEY = "user_user_by_id_%s";

    // for client getting token privilege
    public static final String OAUTH_AUTHORIZATION_CODE = "authorization_code";
    public static final String OAUTH_REFRESH_TOKEN = "refresh_token";
    public static final String OAUTH_IMPLICIT = "implicit";
    public static final String OAUTH_CLIENT_CREDENTIALS = "client_credentials";
    public static final String OAUTH_PASSWORD = "password";




}
