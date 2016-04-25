package com.zitech.gateway.apiconfig.model;

import java.util.Date;

public class Param {
    private Integer id;

    private String requestDemo;

    private String requestStructure;

    private Date createdTime;

    private Integer updatedId;

    private Date updatedTime;

    private Byte deleted;

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