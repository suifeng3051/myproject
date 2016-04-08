package com.zitech.gateway.apiconfig.service.imp;

import com.zitech.gateway.AbstractJunit;
import com.zitech.gateway.apiconfig.model.CarmenApi;
import com.zitech.gateway.apiconfig.model.CarmenApiMethodMapping;
import com.zitech.gateway.apiconfig.model.CarmenServiceMethod;
import com.zitech.gateway.apiconfig.service.ICarmenApiMethodMappingService;
import com.zitech.gateway.apiconfig.service.ICarmenApiService;
import com.zitech.gateway.apiconfig.service.ICarmenServiceMethodService;

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
public class CarmenApiMethodMappingServiceTest extends AbstractJunit {

    @Resource
    private ICarmenApiService apiService;

    @Resource
    private ICarmenServiceMethodService methodService;

    @Resource
    private ICarmenApiMethodMappingService apiMethodMappingService;

    private long apiId;

    private long methodId;

    private long id;

    private String apiSql = "delete from t_api where id=";

    private String methodSql = "delete from t_service_method where id=";

    private String apiMethodMappingSql = "delete from t_api_method_mapping where id=";

    @Before
    public void setUp() throws Exception {
        CarmenServiceMethod methodConfig = new CarmenServiceMethod();
        methodConfig.setName("youzan.account");
        methodConfig.setMethod("generateAccount");
        methodConfig.setVersion("1.0.0");
        methodConfig.setCreateTime(new Date());
        methodConfig.setEnv((byte) 2);
        methodId = methodService.insert(methodConfig);
        CarmenApi config = new CarmenApi();
        config.setNamespace("accountService");
        config.setName("createAccount");
        config.setVersion("1.0.0");
        config.setApiType((byte) 1);
        //config.setAddressUrl("dubbo://127.0.0.1");
        config.setCreateTime(new Date());
        config.setEnv((byte) 2);
        apiId = apiService.insert(config);

        CarmenApiMethodMapping apiMethodMapping = new CarmenApiMethodMapping();
        apiMethodMapping.setServiceMethodId(methodId);
        apiMethodMapping.setApiId(apiId);
        apiMethodMapping.setCreateTime(new Date());
        apiMethodMapping.setEnv((byte) 2);

        id = apiMethodMappingService.insert(apiMethodMapping);

    }

    @After
    public void tearDown() throws Exception {
        innerDelete1(apiId);
        innerDelete2(methodId);
        innerDelete3(id);
    }

    @Test
    public void testInsert() throws Exception {
        CarmenApiMethodMapping apiMethodMapping = new CarmenApiMethodMapping();
        apiMethodMapping.setServiceMethodId(100L);
        apiMethodMapping.setApiId(101L);
        apiMethodMapping.setCreateTime(new Date());
        apiMethodMapping.setEnv((byte) 2);
        long id = 0;
        try {
            id = apiMethodMappingService.insert(apiMethodMapping);
            CarmenApiMethodMapping mapping = apiMethodMappingService.getById(id);
            assertEquals(100L, mapping.getServiceMethodId().longValue());
        } finally {
            if (id != 0) {
                innerDelete3(id);
            }
        }
    }

    @Test
    public void testUpdate() throws Exception {
        CarmenApiMethodMapping apiMethodMapping = apiMethodMappingService.getById(id);
        apiMethodMapping.setModifier("admin");
        apiMethodMappingService.update(apiMethodMapping);
        apiMethodMapping = apiMethodMappingService.getById(id);
        assertEquals("admin", apiMethodMapping.getModifier());
    }


    @Test
    public void testGetById() throws Exception {
        CarmenApiMethodMapping mapping = apiMethodMappingService.getById(id);
        assertNotNull(mapping);
        assertEquals(methodId, mapping.getServiceMethodId().longValue());
        assertEquals(apiId, mapping.getApiId().longValue());
    }

    @Test
    public void testGetByAppId() throws Exception {
        CarmenApiMethodMapping mapping = apiMethodMappingService.getByApiId(apiId, (byte) 2);
        assertNotNull(mapping);
        assertEquals(methodId, mapping.getServiceMethodId().longValue());
        assertEquals(apiId, mapping.getApiId().longValue());
    }

    @Test
    public void testGetByServiceMethodId() throws Exception {
        CarmenApiMethodMapping
                mapping =
                apiMethodMappingService.getByServiceMethodId(methodId, (byte) 2);
        assertNotNull(mapping);
        assertEquals(methodId, mapping.getServiceMethodId().longValue());
        assertEquals(apiId, mapping.getApiId().longValue());
    }

    @Test
    public void testGetByIds() throws Exception {
        CarmenApiMethodMapping
                mapping =
                apiMethodMappingService.getByIds(apiId, methodId, (byte) 2);
        assertNotNull(mapping);
        assertEquals(methodId, mapping.getServiceMethodId().longValue());
        assertEquals(apiId, mapping.getApiId().longValue());
    }
    @Test
    public void testCache()throws Exception{
        List<Long> apiIdList = apiMethodMappingService.getApiIdList((byte) 2);
        assertNotNull(apiIdList);
    }

    private void innerDelete1(long id) {
        apiService.deleteById(id);
        masterJdbcTemplate1.execute(apiSql + id);
    }

    private void innerDelete2(long id) {
        methodService.deleteById(id);
        masterJdbcTemplate1.execute(methodSql + id);
    }

    private void innerDelete3(long id) {
        apiMethodMappingService.deleteById(id);
        apiMethodMappingService.physicalDelete(id);
//    masterJdbcTemplate1.execute(apiMethodMappingSql + id);
    }

}
