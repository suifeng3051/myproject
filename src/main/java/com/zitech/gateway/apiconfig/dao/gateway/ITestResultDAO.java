package com.zitech.gateway.apiconfig.dao.gateway;

import com.zitech.gateway.apiconfig.model.CarmenTestResult;
import com.zitech.platform.dao.base.func.IEntityDAO;
import org.apache.ibatis.annotations.Param;

public interface ITestResultDAO extends IEntityDAO<CarmenTestResult,CarmenTestResult> {

   CarmenTestResult getByRefApiId(@Param("refApiId") long refApiId);
}