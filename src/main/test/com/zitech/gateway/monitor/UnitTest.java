/*
 * Copyright (c) 2015-2020 by zitech
 * All rights reserved.
 */
package com.zitech.gateway.monitor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: UnitTest 
 * @author ws
 * @date 2015年12月21日 下午7:49:11 
 * @Copyright (c) 2015-2020 by zitech
 */
public class UnitTest {

	@Test
	public void test() throws ParseException {
		SimpleDateFormat timeSdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date dateStart = timeSdf.parse("20151221162329");

        int year = dateStart.getYear();
        int month = dateStart.getMonth();
        int date = dateStart.getDate();
        int hour = dateStart.getHours();
        int minutes = dateStart.getMinutes();
       
        double startTime = Math.floor(new Date(year, month, date, 0, 0, 0).getTime()/1000);
        System.out.println(new Date(year, month, date, 0, 0, 0).getTime());
        double endTime = Math.floor(new Date().getTime() / 1000); // 获取毫秒时间
        System.out.println(new Date().getTime());
        System.out.println(startTime);
        System.out.println(endTime);
	}

	@Test
	public void testMillToDate(){
		long sd=1450840113000L;  
        Date dat=new Date(sd);  
        GregorianCalendar gc = new GregorianCalendar();   
        gc.setTime(dat);  
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");  
        String sb=format.format(gc.getTime());  
        System.out.println(sb);  
	}
	
}
