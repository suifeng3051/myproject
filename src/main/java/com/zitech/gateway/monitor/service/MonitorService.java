/*
 * Copyright (c) 2015-2020 by zitech
 * All rights reserved.
 */
package com.zitech.gateway.monitor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zitech.gateway.gateway.model.RequestEvent;
import com.zitech.gateway.monitor.api.IMonitorService;
import com.zitech.gateway.monitor.entity.Gateway;
import com.zitech.gateway.monitor.mongodb.MongoBaseDao;
import com.zitech.gateway.utils.AppUtils;
import com.zitech.gateway.utils.DateUtil;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: MonitorService 
 * @author ws
 * @date 2015年11月27日 上午10:10:45 
 * @Copyright (c) 2015-2020 by zitech
 */
@Service
public class MonitorService implements IMonitorService {
	
	@Autowired
	MongoBaseDao dao;
	
	private static final Logger logger = LoggerFactory.getLogger(MonitorService.class);

	
	public void saveMonitorData(String path, long rt, long serveRt, int status, String other) {
		Gateway gateway = new Gateway();
		try {
			gateway.setApi(path);
            gateway.setHost(AppUtils.getHostName());

            String time = DateUtil.getTimeNumber();
            gateway.setMinute(time.substring(0,time.length()-2));//记录到分
            gateway.setTime(time);

			gateway.setRt(String.valueOf(rt));
            gateway.setServeRt(String.valueOf(serveRt));
            gateway.setStatus(String.valueOf(status));
            gateway.setOther(other);

			dao.add(gateway);
		} catch (Exception e) {
			logger.error("网关数据存储异常, 网关数据：{}", gateway.toString(), e);
		}
		
	}

}
