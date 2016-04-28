/*
 * Copyright (c) 2015-2020 by zitech
 * All rights reserved.
 */
package com.zitech.gateway.monitor.mongodb;

import java.util.List;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: BaseDao 
 * @author ws
 * @date 2015年11月26日 上午11:34:40 
 * @Copyright (c) 2015-2020 by zitech
 */
public interface IBaseDao {
	  
    <T> T findById(Class<T> entityClass, String id);  
  
    <T> List<T> findAll(Class<T> entityClass);  
  
    void remove(Object obj);  
  
    void add(Object obj);  
  
    void saveOrUpdate(Object obj); 
}
