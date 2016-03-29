package com.zitech.gateway.apiconfig.service.imp;

import com.zitech.gateway.AbstractJunit;
import com.zitech.gateway.apiconfig.model.CarmenApi;
import com.zitech.gateway.apiconfig.model.CarmenApiParam;
import com.zitech.gateway.apiconfig.service.ICarmenApiParamService;
import com.zitech.gateway.apiconfig.service.ICarmenApiService;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by bobo on 10/20/15.
 */
public class CarmenApiParamServiceTest extends AbstractJunit {

    @Resource
    private ICarmenApiService apiService;
    @Resource
    private ICarmenApiParamService apiParamService;

    private long apiId;

    private long id;

    private String apiSql = "delete from t_api where id=";

    private String apiParamSql = "delete from t_api_param where id=";

    @BeforeClass
    public static void init() throws Exception {

    }


    @AfterClass
    public static void destroy() throws Exception {

    }


    @Before
    public void setUp() throws Exception {
        CarmenApi config = new CarmenApi();
        config.setNamespace("accountService");
        config.setName("createAccount_param");
        config.setVersion("1.0.0");
        config.setApiType((byte) 1);
        config.setAddressUrl("dubbo://127.0.0.1");
        config.setCreateTime(new Date());
        config.setEnv((byte) 2);
        apiId = apiService.insert(config);
        CarmenApiParam param = new CarmenApiParam();
        param.setParamName("accountId");
        param.setParamType("number");
        param.setIsRequired((byte) 1);
        param.setEnv((byte) 2);
        param.setIsStructure((byte) 0);
        param.setCreateTime(new Date());
        param.setSequence(1);
        param.setApiId(apiId);
        id = apiParamService.insert(param);

    }

    @After
    public void tearDown() throws Exception {
        innerDelete1(id);
    }

    @Test
    public void testInsert() throws Exception {
        CarmenApiParam param = new CarmenApiParam();
        param.setParamName("password");
        param.setParamType("string");
        param.setIsRequired((byte) 1);
        param.setEnv((byte) 2);
        param.setIsStructure((byte) 0);
        param.setCreateTime(new Date());
        param.setSequence(2);
        param.setApiId(apiId);
        long id = 0;
        try {
            id = apiParamService.insert(param);
            CarmenApiParam paramChange = apiParamService.getById(id);
            assertEquals("password", paramChange.getParamName());
        } finally {
            if (id != 0) {
                innerDelete1(id);
            }
        }

    }

    @Test
    public void testUpdate() throws Exception {
        CarmenApiParam param = apiParamService.getById(id);
        param.setParamType("string");
        param.setSequence(3);
        apiParamService.update(param);
        param = apiParamService.getById(id);
        assertEquals("string", param.getParamType());
        assertEquals(3, param.getSequence().intValue());
    }

    @Test
    public void testGetById() throws Exception {
        CarmenApiParam param = apiParamService.getById(id);
        assertNotNull(param);
    }

    @Test
    public void testBatchSave()throws Exception{
        List<CarmenApiParam> list=new ArrayList<>();
        apiParamService.batchSave(list);
    }

    @Test
    public void testGetByApiId() throws Exception {
        List<CarmenApiParam> carmenApiParamList = apiParamService.getByApiId(apiId, (byte) 2);
        int size = carmenApiParamList.size();
        assertEquals(1, size);
        assertEquals("accountId", carmenApiParamList.get(0).getParamName());
    }

    @Test
    public void testCache()throws Exception{
        List<Long> apiIdList = apiParamService.getApiIdList((byte) 2);
        assertNotNull(apiIdList);
    }
    private void innerDelete1(long id) {
        apiParamService.deleteById(id);
        apiParamService.physicalDelete(id);
//    masterJdbcTemplate1.execute(apiParamSql + id);
    }

}