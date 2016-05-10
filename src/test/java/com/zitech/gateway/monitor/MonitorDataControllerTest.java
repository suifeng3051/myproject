package com.zitech.gateway.monitor;

import com.zitech.gateway.AbstractJunit;
import com.zitech.gateway.monitor.entity.CountResult;
import com.zitech.gateway.monitor.mapreduce.MapReduceJs;
import com.zitech.gateway.monitor.mongodb.MapReduceDao;
import com.zitech.gateway.utils.DateUtil;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@Service
public class MonitorDataControllerTest extends AbstractJunit {

    @Autowired
    MapReduceDao mapReduceDao;

    @Test
    public void testGetMonitorData() throws Exception {

        String group = "";
        String api = "";
        String host = "";

        String stime = "";
        String etime = "";

        Calendar c1 = Calendar.getInstance();
        c1.set(2016-1900, Calendar.MAY, 1);

        Calendar c2 = Calendar.getInstance();
        c2.set(2016-1900, Calendar.MAY, 10);

        String beginTime = DateUtil.date2String(c1.getTime(), "yyyyMMddHHmm");
        String endTime = DateUtil.date2String(c2.getTime(), "yyyyMMddHHmm");

        List<Object> response = new ArrayList<Object>();
        Map<String,Object> resMap = new HashMap<String, Object>();
        List<Object> result = new ArrayList<Object>();

        Query query = createQueryConditions(beginTime, endTime, group, api, host);

        String inputCollectionName = "gateway";
        String mapFunction = MapReduceJs.MAP_FAILURE_COUNT;
        String reduceFunction = MapReduceJs.REDUCE_FAILURE_COUNT;

        MapReduceResults<CountResult> rtResult = mapReduceDao.command(query
                , inputCollectionName, mapFunction, reduceFunction);
    }

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