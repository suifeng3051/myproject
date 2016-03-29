package com.zitech.gateway.apiconfig.controller;

import com.zitech.gateway.apiconfig.model.CarmenApi;
import com.zitech.gateway.apiconfig.model.CarmenApiParam;
import com.zitech.gateway.apiconfig.model.CarmenUser;
import com.zitech.gateway.apiconfig.service.ICarmenApiParamService;
import com.zitech.gateway.apiconfig.service.ICarmenApiService;
import com.zitech.gateway.apiconfig.service.ICarmenUserService;
import com.zitech.gateway.cache.RedisOperate;

import org.apache.struts.mock.MockHttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import mockit.Expectations;
import mockit.Injectable;
import mockit.NonStrictExpectations;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

import static org.junit.Assert.*;

/**
 * Created by bobo on 10/20/15.
 */

@RunWith(JMockit.class)
public class ApiDetailControllerTest {

    @Tested
    ApiDetailController apiDetailController;

    @Injectable
    private ICarmenApiService iCarmenApiService;
    @Injectable
    private RedisOperate redisOperate;
    @Injectable
    private ICarmenApiParamService iCarmenApiParamService;
    @Injectable
    private ICarmenUserService iCarmenUserService;


    @Test
    public void testIsAdministrator() throws Exception {
        new Expectations(){{
            iCarmenUserService.getByUserName("dingdongsheng");
            CarmenUser carmenUser = new CarmenUser();
            carmenUser.setUserGroup(1);
            List<CarmenUser> carmenUserList = new ArrayList<>();
            carmenUserList.add(carmenUser);
            result = carmenUserList;
        }};

        Boolean result = apiDetailController.isAdministrator("dingdongsheng");
        Assert.assertEquals((Boolean) true, result);
    }

    @Test
    public void testIsAdministrator_Invalid() throws Exception {
        new Expectations(){{
            iCarmenUserService.getByUserName("dingdongsheng");
            Exception e = new Exception();
            result = e;
        }};

        Boolean result = apiDetailController.isAdministrator("dingdongsheng");
        Assert.assertEquals(false, result);
    }


    @Test
    public void testApiDetail() throws Exception {
        new NonStrictExpectations() {
            {
                redisOperate.getStringByKey("dds");
                result = "test";

                redisOperate.set("username", "dingdongsheng", 1800);

                iCarmenApiService.getById(10);
                CarmenApi carmenApi = new CarmenApi();
                carmenApi.setApiGroup("test");
                carmenApi.setId(100l);
                result = carmenApi;

                iCarmenApiParamService.getByApiId(10,(byte)1);

                List<CarmenApiParam> carmenApiParamList = new ArrayList<>();
                CarmenApiParam one = new CarmenApiParam();
                one.setId(100l);

                CarmenApiParam two = new CarmenApiParam();
                two.setId(200l);
                carmenApiParamList.add(one);
                carmenApiParamList.add(two);


            }
        };

        HttpServletRequest request = new MockHttpServletRequest();
        request.getSession(true).setAttribute("username", "test");
        ModelAndView result = apiDetailController.apiDetail("1", "1", request);
    }

    @Test
    public void testApiDetail_null_session() throws Exception {
        new NonStrictExpectations() {
            {
                redisOperate.getStringByKey("dds");
                result = "test";

                redisOperate.set("username", "dingdongsheng", 1800);

                iCarmenApiService.getById(10);
                CarmenApi carmenApi = new CarmenApi();
                carmenApi.setApiGroup("test");
                carmenApi.setId(100l);
                result = carmenApi;

                iCarmenApiParamService.getByApiId(10,(byte)1);

                List<CarmenApiParam> carmenApiParamList = new ArrayList<>();
                CarmenApiParam one = new CarmenApiParam();
                one.setId(100l);

                CarmenApiParam two = new CarmenApiParam();
                two.setId(200l);
                carmenApiParamList.add(one);
                carmenApiParamList.add(two);


            }
        };

        HttpServletRequest request = new MockHttpServletRequest();
        ModelAndView result = apiDetailController.apiDetail("1", "1", request);
    }

}