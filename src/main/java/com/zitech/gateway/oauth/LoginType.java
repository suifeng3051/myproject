package com.zitech.gateway.oauth;

/**
 * Created by ws on 2015/10/29.
 */
public enum LoginType {
    USERNAME("0"),PHONE("1"),EMAIL("2");

    private String value;
    LoginType(String value)
    {
        this.value = value;
    }
    public String getValue(){return value;}
}
