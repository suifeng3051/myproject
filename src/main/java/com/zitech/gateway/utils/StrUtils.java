package com.zitech.gateway.utils;

/**
 *
 * 判断2个字符串是否相等，都为空认为相等
 * Created by Lion on 2015/11/9 0009.
 */
public class StrUtils {

    public static boolean equals(String str1, String str2)
    {
        if(str1 == null && str2 == null)
        {
            return true;
        }
        else {
            if(str1 == null)
            {
                return false;
            }
            else {
                return str1.equals(str2);
            }
        }
    }
    /**
     * JSON字符串特殊字符处理，比如：“\A1;1300”
     * @param s
     * @return String
     */
    public String jsonEscapeSpecialCharacters(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<s.length(); i++) {
            char c = s.charAt(i);
            switch (c){
                case '\"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }
}
