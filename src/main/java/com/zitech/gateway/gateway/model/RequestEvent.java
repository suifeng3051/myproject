package com.zitech.gateway.gateway.model;

import com.zitech.gateway.apiconfig.model.Api;
import com.zitech.gateway.gateway.Constants;
import com.zitech.gateway.gateway.excutor.TicTac;
import com.zitech.gateway.oauth.model.AccessToken;

import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;


public class RequestEvent {

    private UUID id = UUID.randomUUID();

    private DeferredResult<Object> result;
    private HttpServletRequest request;

    private String requestType;

    private AccessToken accessToken;
    private String ip;

    private String namespace;
    private String method;
    private Integer version;

    private String body;

    private Api api;

    private TicTac ticTac = new TicTac();

    private String resultStr;
    private Exception exception;

    public RequestEvent() {
    }

    public RequestEvent(DeferredResult<Object> result, HttpServletRequest request) {
        this.result = result;
        this.request = request;
        this.requestType = request.getMethod();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public DeferredResult<Object> getResult() {
        return result;
    }

    public void setResult(DeferredResult<Object> result) {
        this.result = result;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Api getApi() {
        return api;
    }

    public void setApi(Api api) {
        this.api = api;
    }

    public TicTac getTicTac() {
        return ticTac;
    }

    public void setTicTac(TicTac ticTac) {
        this.ticTac = ticTac;
    }

    public String getResultStr() {
        return resultStr;
    }

    public void setResultStr(String resultStr) {
        this.resultStr = resultStr;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "[ " + id + " " + requestType + " " + this.getNamespace() + " " + this.getMethod() + " " + this.getVersion() + " " + " ] {" +
                ", accessToken='" + accessToken + '\'' +
                ", body='" + body + '\'' +
                ", exception=" + (exception == null ? "" : exception.toString()) +
                ", clientIp = '" + (getIp() == null ? "" : getIp()) + '\'' +
                '}';
    }

}
