package com.zitech.gateway.apiconfig.service.imp;

import com.zitech.gateway.AbstractJunit;
import com.zitech.gateway.apiconfig.model.CarmenApi;
import com.zitech.gateway.apiconfig.service.ICarmenApiService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import static org.junit.Assert.*;

/**
 * Created by newlife on 16/3/29.
 */
public class CarmenApiServiceTest extends AbstractJunit {


    @Resource
    private ICarmenApiService apiService;

    private long id;

    private String apiSql = "delete from carmen_api where id=";
    @Before
    public void setUp() throws Exception {
        CarmenApi config=new CarmenApi();
        config.setNamespace("accountService2");
        config.setName("createAccount");
        config.setApiGroup("test");
        config.setApiDesc("xxxxxxxxxxxxx");
        config.setApiScenarios("xxxxxxxxxyyyyyy");
        config.setVersion("1.0.0");
        config.setApiType((byte) 1);
        //config.setAddressUrl("dubbo://127.0.0.1");
        config.setCreateTime(new Date());
        config.setValidFlag((byte) 2);
        config.setTestFlag((byte) 2);
        config.setMigrateFlag((byte) 2);
        config.setSessionFlag((byte)1);
        config.setEnv((byte)2);
        id=apiService.insert(config);
    }

    @After
    public void tearDown() throws Exception {
        innerDelete(id);
    }

    @Test
    public void testInsert() throws Exception {
        CarmenApi config=new CarmenApi();
        config.setNamespace("accountService");
        config.setName("createAccount");
        config.setApiGroup("test");
        config.setApiDesc("xxxxxxxxxxxxx");
        config.setApiScenarios("xxxxxxxxxyyyyyy");
        config.setVersion("1.0.0");
        config.setApiType((byte) 1);
        //config.setAddressUrl("dubbo://127.0.0.2");
        config.setCreateTime(new Date());
        config.setSessionFlag((byte)1);
        config.setEnv((byte)2);
        long id=0;
        try {
            id=apiService.insert(config);
            CarmenApi configChange=apiService.getById(id);
            assertEquals("1.0.0",configChange.getVersion());
        } finally {
            if (id!=0){
                innerDelete(id);
            }
        }
    }

    @Test
    public void testUpdate() throws Exception {
        CarmenApi config=apiService.getById(id);
        config.setName("modifyAccount");
        config.setSessionFlag((byte) 2);
        apiService.update(config);
        config=apiService.getById(id);
        assertEquals("modifyAccount", config.getName());
        assertEquals((byte) 2, config.getSessionFlag().byteValue());
    }

    @Test
    public void testGetByenv(){
        List<CarmenApi> list=apiService.getRecordByEnv((byte) 2);
        assertNotNull(list);
    }

    @Test
    public void testGetById() throws Exception {
        CarmenApi config=apiService.getById(id);
        assertEquals("1.0.0", config.getVersion());
    }

    @Test
    public void testGetByGroup(){
        CarmenApi config=apiService.getById(id);
        List<CarmenApi> list=apiService.getRecordByEnvGroup(config.getEnv(), config.getApiGroup());
        assertNotNull(list);
    }

    @Test
    public void testGetRecordByCondition() throws Exception {
        CarmenApi config= apiService.getRecordByCondition("accountService2", "createAccount", "1.0.0",
                (byte) 2);
        assertNotNull(config);
    }

    @Test
    public void getDeletedRecord(){
        List<CarmenApi> list=apiService.getAllDeletedRecord();
        if (list!=null){
            assertNotNull(list);
        }
    }

    private void innerDelete(long id){
        apiService.deleteById(id);
        apiService.physicalDelete(id);
//    masterJdbcTemplate1.execute(apiSql+id);
    }


}
