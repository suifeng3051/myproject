package com.zitech.gateway.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

public class ApiResult<T> {

    private static final SimplePropertyPreFilter thisFilter = new SimplePropertyPreFilter(
            ApiResult.class, "code", "message", "data");

    private int code;
    private String message;
    private T data;
    private SerializeFilter[] filters = new SerializeFilter[2];

    public ApiResult() {
        this.filters[0] = thisFilter;
    }

    public ApiResult(int code, String message) {
        this.filters[0] = thisFilter;
        this.code = code;
        this.message = message;
    }

    public ApiResult(int code, String message, T data) {
        this.filters[0] = thisFilter;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ApiResult(int code, String message, T data, SimplePropertyPreFilter filter) {
        this.filters[0] = thisFilter;
        this.filters[1] = filter;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, this.filters);
    }
}
