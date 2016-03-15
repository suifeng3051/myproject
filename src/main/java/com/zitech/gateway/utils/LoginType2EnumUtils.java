package com.zitech.gateway.utils;

import com.zitech.gateway.oauth.LoginType;

/**
 * Created by ws on 2015/10/29.
 */
public class LoginType2EnumUtils {
    public static LoginType getLoginTypeByStr(String str) {
        if (str == null)
            return null;
        LoginType loginTypes[] = LoginType.values();
        for (LoginType loginType : loginTypes) {
            if (loginType.getValue().equals(str)) {
                return loginType;
            }
        }
        return null;
    }
}
