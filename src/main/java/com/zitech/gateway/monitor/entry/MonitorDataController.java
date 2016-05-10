/*
 * Copyright (c) 2015-2020 by zitech
 * All rights reserved.
 */
package com.zitech.gateway.monitor.entry;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zitech.gateway.monitor.entity.CountResult;
import com.zitech.gateway.monitor.mapreduce.MapReduceJs;
import com.zitech.gateway.utils.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.zitech.gateway.monitor.mongodb.MapReduceDao;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: MonitorController 
 * @author ws
 * @date 2015年11月27日 上午11:29:26 
 * @Copyright (c) 2015-2020 by zitech
 */
@Controller
public class MonitorDataController {

	private static final Logger logger = LoggerFactory.getLogger(MonitorDataController.class);

    private static final String DB_NAME = "gateway";

	@Autowired
	MapReduceDao mapReduceDao;
	
	/**
	 * 每分钟RT平均时间统计
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: rtCount 
	 * @return
	 * @date 2015年11月27日 下午2:20:26  
	 * @author ws
	 * @param metric
	 * @param etime 
	 * @param stime 
	 */
	@RequestMapping(value = "/querymonitor", produces = "application/json;charset=utf-8")
	@ResponseBody
    public String getMonitorData(String stime, String etime, String metric, String group, String host, String api){
		
    	Date beginDate = new Date(Long.parseLong(stime)*1000);
    	Date endDate = new Date(Long.parseLong(etime)*1000);
    	String beginTime = DateUtil.date2String(beginDate, "yyyyMMddHHmm");
		String endTime = DateUtil.date2String(endDate, "yyyyMMddHHmm");
    	
		List<Object> response = new ArrayList<Object>();
		Map<String,Object> resMap = new HashMap<String, Object>();
		List<Object> result = new ArrayList<Object>();

		Query query = createQueryConditions(beginTime, endTime, group, api, host);
		
		String inputCollectionName = DB_NAME;
		String mapFunction = MapReduceJs.MAP_FAILURE_COUNT;
		String reduceFunction = MapReduceJs.REDUCE_FAILURE_COUNT;
		
		MapReduceResults<CountResult> rtResult = mapReduceDao.command(query
				, inputCollectionName, mapFunction, reduceFunction);
		
		for (CountResult countResult : rtResult) {
			Map<String, Object> res = new HashMap<String, Object>();
			Date keyDate = DateUtil.string2Date(countResult.get_id(), "yyyyMMddHHmm");
			Double keyD = Math.floor(keyDate.getTime()/1000);
			Long key = Math.round(keyD);
			//BigDecimal key = new BigDecimal(keyD);  
			res.put("key", key);
			String value = countResult.getValue();
			if(value.contains("isSuccess")){
				value = value.substring(17, value.length()-2);
			}
			res.put("value", Math.round(Double.parseDouble(value)));
			result.add(res);
		}

		resMap.put("dps", result);
		resMap.put("metric", "failure");
		response.add(resMap);
		
		/*******最大rt时间********/
		result = new ArrayList<Object>();
		resMap = new HashMap<String, Object>();
		inputCollectionName = DB_NAME;
		mapFunction = MapReduceJs.MAP_RT_MAX;
		reduceFunction = MapReduceJs.REDUCE_RT_MAX;
		rtResult = mapReduceDao.command(query
				, inputCollectionName, mapFunction, reduceFunction);
		
		for (CountResult countResult : rtResult) {
			Map<String, Object> res = new HashMap<String, Object>();
			Date keyDate = DateUtil.string2Date(countResult.get_id(), "yyyyMMddHHmm");
			Double keyD = Math.floor(keyDate.getTime()/1000);
			//BigDecimal key = new BigDecimal(keyD);
			Long key = Math.round(keyD);  
			res.put("key", key);
			String value = countResult.getValue();
			if(value.contains("rt")){
				value = value.substring(10, value.length()-2);
			}
			res.put("value", Math.round(Double.parseDouble(value)));
			result.add(res);
		}

		resMap.put("dps", result);
		resMap.put("metric", "maxResponseTime");
		response.add(resMap);
		
		/*******调用次数********/
		result = new ArrayList<Object>();
		resMap = new HashMap<String, Object>();
		inputCollectionName = DB_NAME;
		mapFunction = MapReduceJs.MAP_REQUEST_COUNT;
		reduceFunction = MapReduceJs.REDUCE_REQUEST_COUNT;
		rtResult = mapReduceDao.command(query
				, inputCollectionName, mapFunction, reduceFunction);
		
		for (CountResult countResult : rtResult) {
			Map<String, Object> res = new HashMap<String, Object>();
			Date keyDate = DateUtil.string2Date(countResult.get_id(), "yyyyMMddHHmm");
			Double keyD = Math.floor(keyDate.getTime()/1000);
			//BigDecimal key = new BigDecimal(keyD); 
			Long key = Math.round(keyD);
			res.put("key", key);String value = countResult.getValue();
			if(value.contains("count")){
				value = value.substring(12, value.length()-1);
			}
			res.put("value", Math.round(Double.parseDouble(value)));
			result.add(res);
		}

		resMap.put("dps", result);
		resMap.put("metric", "callCount");
		response.add(resMap);
		
		String responseStr = JSON.toJSONString(response);
		System.out.println(responseStr);
		return responseStr;
		
	}
     
	
	
	/**
	 * 构造查询条件
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: greatQueryConditions 
	 * @param beginTime
	 * @param endTime
	 * @param group
	 * @param api
	 * @param host
	 * @return
	 * @date 2015年11月27日 下午2:19:57  
	 * @author ws
	 */
	private Query createQueryConditions(String beginTime, String endTime,
                                        String group, String api, String host) {
		Query query = new Query(Criteria.where("minute").gte(beginTime).lte(endTime));
		if(StringUtils.isNotBlank(group)){
            query.addCriteria(Criteria.where("group").regex(".*?"+group+".*"));
		}
		if(StringUtils.isNotBlank(api)){
			query.addCriteria(Criteria.where("api").regex(".*?"+api+".*"));
		}
		if(StringUtils.isNotBlank(host)){
			query.addCriteria(Criteria.where("host").regex(".*?"+host+".*"));
		}
		//query.with(new Sort(new Order(Direction.ASC, "minute")));//升序
		return query;
	}
	
	
	
}
