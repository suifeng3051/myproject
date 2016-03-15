package com.zitech.gateway.frequency.service;

import com.zitech.gateway.frequency.ExpiredDataType;

/**
 * Created by chenyun on 15/8/5.
 */
public interface ICarmenExpiredQueue {

    /**
     * 过期数据进入队列
     */
    boolean ExpiredDataEnqueue(String context, ExpiredDataType type);
}
