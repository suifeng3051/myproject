/*
 * Copyright (c) 2015-2020 by zitech
 * All rights reserved.
 */
package com.zitech.gateway.monitor.api;

import com.zitech.gateway.gateway.model.RequestEvent;
import com.zitech.gateway.monitor.entity.Gateway;


public interface IMonitorService {
	
	void saveMonitorData(String path, long rt, long serveRt, int status, String other);
}
