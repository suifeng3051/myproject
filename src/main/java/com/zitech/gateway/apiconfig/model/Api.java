package com.zitech.gateway.apiconfig.model;

import com.zitech.gateway.common.RequestType;

import java.util.Date;

public class Api {
    private Integer id;

    private String namespace;

    private String method;

    private Integer version;

    private Integer avail;

    private Integer groupId;

    private RequestType requestType;

    private Byte frequencyControl;

    private Byte checkInner;

    private String apiDescription;

    private String apiScene;

    private Date createdTime;

    private Integer updatedId;

    private Date updatedTime;

    private Byte deleted;

    private String resultDemo;
    private Byte env;

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public Byte getEnv() {
        return env;
    }

    public void setEnv(Byte env) {
        this.env = env;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace == null ? null : namespace.trim();
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method == null ? null : method.trim();
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getAvail() {
        return avail;
    }

    public void setAvail(Integer avail) {
        this.avail = avail;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Byte getFrequencyControl() {
        return frequencyControl;
    }

    public void setFrequencyControl(Byte frequencyControl) {
        this.frequencyControl = frequencyControl;
    }

    public Byte getCheckInner() {
        return checkInner;
    }

    public void setCheckInner(Byte checkInner) {
        this.checkInner = checkInner;
    }

    public String getApiDescription() {
        return apiDescription;
    }

    public void setApiDescription(String apiDescription) {
        this.apiDescription = apiDescription == null ? null : apiDescription.trim();
    }

    public String getApiScene() {
        return apiScene;
    }

    public void setApiScene(String apiScene) {
        this.apiScene = apiScene == null ? null : apiScene.trim();
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Integer getUpdatedId() {
        return updatedId;
    }

    public void setUpdatedId(Integer updatedId) {
        this.updatedId = updatedId;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Byte getDeleted() {
        return deleted;
    }

    public void setDeleted(Byte deleted) {
        this.deleted = deleted;
    }

    public String getResultDemo() {
        return resultDemo;
    }

    public void setResultDemo(String resultDemo) {
        this.resultDemo = resultDemo == null ? null : resultDemo.trim();
    }
}