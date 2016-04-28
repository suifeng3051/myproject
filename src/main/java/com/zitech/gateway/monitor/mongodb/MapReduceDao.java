/*
 * Copyright (c) 2015-2020 by zitech
 * All rights reserved.
 */
package com.zitech.gateway.monitor.mongodb;

import com.zitech.gateway.monitor.entity.CountResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: MongoBaseDao 
 * @author ws
 * @param <T>
 * @date 2015年11月26日 上午11:36:56 
 * @Copyright (c) 2015-2020 by zitech
 */
@Repository
public class MapReduceDao {
	@Autowired  
    protected MongoTemplate mongoTemplate;  
  
    public MapReduceResults<CountResult> command(Query query, String inputCollectionName
    		, String mapFunction, String reduceFunction){
    	
    	//CommandResult result = mongoTemplate.executeCommand(command);
    	
    	MapReduceResults<CountResult> result = mongoTemplate.mapReduce(query
    			, inputCollectionName, mapFunction, reduceFunction, CountResult.class);
    	
    	return result;
    }
	
	
    public MongoTemplate getMongoTemplate() {  
        return mongoTemplate;  
    }  
      
}
