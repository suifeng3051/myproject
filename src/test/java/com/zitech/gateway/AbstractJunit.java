/*
 * Copyright (c) 2015-2020 by zitech
 * All rights reserved.
 */
package com.zitech.gateway;

import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.annotation.Resource;


/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: AbstractJunit 
 * @author ws
 * @date 2015年11月3日 上午11:03:58 
 * @Copyright (c) 2015-2020 by zitech
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring-test.xml"})
@TransactionConfiguration(defaultRollback=true,transactionManager="txManagerCommon")
@ActiveProfiles("dev")
public abstract class AbstractJunit {

    @Resource
    protected JdbcTemplate masterJdbcTemplate;

    @Resource
    protected JdbcTemplate masterJdbcTemplate1;

}
