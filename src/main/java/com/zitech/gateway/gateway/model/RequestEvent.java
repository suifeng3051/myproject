package com.zitech.gateway.gateway.model;

import com.zitech.gateway.apiconfig.model.Api;
import com.zitech.gateway.common.RequestType;
import com.zitech.gateway.gateway.Constants;
import com.zitech.gateway.gateway.PipeHelper;
import com.zitech.gateway.gateway.excutor.TicTac;
import com.zitech.gateway.oauth.model.AccessToken;

import org.springframework.web.context.request.async.DeferredResult;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;


public class RequestEvent {

    public final UUID uuid = UUID.randomUUID();

    private DeferredResult<Object> result;
    private HttpServletRequest request;

    private RequestType requestType;

    private AccessToken accessToken;
    private String ip;

    private String namespace;
    private String method;
    private Integer version;

    private String body;

    private Api api;

    private String resultStr;
    private Exception exception;

    private Map<String, String> contextMap = new HashMap<>();

    private TicTac ticTac = new TicTac();

    private Integer step = -1;

    public RequestEvent() {
    }

    public RequestEvent(DeferredResult<Object> result, HttpServletRequest request) {
        this.result = result;
        this.request = request;
        this.requestType = RequestType.from(request.getMethod());
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

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
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

    public Map<String, String> getContextMap() {
        return contextMap;
    }

    public void setContextMap(Map<String, String> contextMap) {
        this.contextMap = contextMap;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    @Override
    public String toString() {
        return "{" +
                "uuid=" + uuid +
                ", requestType=" + requestType +
                ", accessToken=" + request.getParameter(Constants.ACCESS_TOKEN) +
                ", ip='" + (ip == null ? "" : ip) + '\'' +
                ", path='" + namespace + "/" + version + "/" + method + '\'' +
                ", step=" + step +
                ", body='" + PipeHelper.removeSpaces(body) + '\'' +
                ", resultStr='" + PipeHelper.removeSpaces(resultStr) + '\'' +
                ", exception=" + (exception == null ? "" : exception.getMessage()) +
                '}';
    }
}
