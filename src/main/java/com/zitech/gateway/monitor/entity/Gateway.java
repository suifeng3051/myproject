/*
 * Copyright (c) 2015-2020 by zitech
 * All rights reserved.
 */
package com.zitech.gateway.monitor.entity;

import org.springframework.data.mongodb.core.mapping.Document;


@Document
public class Gateway {
    private String api;
    private String host;
    private String time;
    private String minute;
    private String rt;
    private String serveRt;
    private String status;
    private String other;

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRt() {
        return rt;
    }

    public void setRt(String rt) {
        this.rt = rt;
    }

    public String getServeRt() {
        return serveRt;
    }

    public void setServeRt(String serveRt) {
        this.serveRt = serveRt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    @Override
    public String toString() {
        return "{" +
                "api='" + api + '\'' +
                ", host='" + host + '\'' +
                ", time='" + time + '\'' +
                ", minute='" + minute + '\'' +
                ", rt='" + rt + '\'' +
                ", serveRt='" + serveRt + '\'' +
                ", status='" + status + '\'' +
                ", other='" + other + '\'' +
                '}';
    }
}
