/*
 * Copyright (c) 2015-2020 by zitech
 * All rights reserved.
 */
package com.zitech.gateway.monitor.mapreduce;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: MapReduceJs 
 * @author ws
 * @date 2015年11月27日 上午9:50:21 
 * @Copyright (c) 2015-2020 by zitech
 */
public interface MapReduceJs {
	
	String MAP_FAILURE_COUNT = "function (){emit(this.minute,{status:this.status});}";
	String REDUCE_FAILURE_COUNT = "function (key,values){var count=0;values.forEach(function(val){if('1'==val.status){count +=1;}});return count;}";
	
	String MAP_RT_MAX = "function (){emit(this.minute,{rt:this.rt});}";
	String REDUCE_RT_MAX = "function (key,values){var ctmax=0; values.forEach(function(val){if(ctmax < val.rt){ctmax = val.rt;}});return ctmax ;}";

	String MAP_REQUEST_COUNT = "function (){emit(this.minute,{count:1});}";
	String REDUCE_REQUEST_COUNT = "function (key,values){var cnt=0;values.forEach(function(val){ cnt+=val.count;});return cnt;}";

	String MAP_RT_COUNT = "function (){emit(this.minute,{rt:this.rt});}";
	String REDUCE_RT_COUNT = "function (key,values){var cnt=1;var rtall=0;values.forEach(function(val){cnt+=1;rtall+=parseInt(val.rt);});return (rtall/cnt).toFixed(2);}";
	
}
