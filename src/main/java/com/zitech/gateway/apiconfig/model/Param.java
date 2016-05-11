package com.zitech.gateway.apiconfig.model;

import java.util.Date;

public class Param {
    private Integer id;

    private Integer apiId;

    private String requestDemo;

    private String requestStructure;

    private String requestMemo;

    private Date createdTime;

    private Integer updatedId;

    private Date updatedTime;

    private Byte deleted;
    private Byte env;

    public Byte getEnv() {
        return env;
    }

    public void setEnv(Byte env) {
        this.env = env;
    }

    public Integer getApiId() {
        return apiId;
    }

    public void setApiId(Integer apiId) {
        this.apiId = apiId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRequestDemo() {
        return requestDemo;
    }

    public void setRequestDemo(String requestDemo) {
        this.requestDemo = requestDemo == null ? null : requestDemo.trim();
    }

    public String getRequestStructure() {
        return requestStructure;
    }

    public void setRequestStructure(String requestStructure) {
        this.requestStructure = requestStructure == null ? null : requestStructure.trim();
    }

    public String getRequestMemo() {
        return requestMemo;
    }

    public void setRequestMemo(String requestMemo) {
        this.requestMemo = requestMemo;
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
}