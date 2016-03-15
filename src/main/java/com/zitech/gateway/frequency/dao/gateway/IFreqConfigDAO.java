package com.zitech.gateway.frequency.dao.gateway;

import com.zitech.gateway.frequency.model.CarmenFreqConfig;
import com.zitech.platform.dao.base.func.IEntityDAO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IFreqConfigDAO extends IEntityDAO<CarmenFreqConfig, CarmenFreqConfig> {

    CarmenFreqConfig getValueByCondition(@Param("type") int type, @Param("apiRef") long apiRef,
                                         @Param("clientId") long clientId);

    CarmenFreqConfig getValueByApiAndType(@Param("type") int type, @Param("apiRef") long apiRef);

    List<CarmenFreqConfig> getListValueByCondition(@Param("apiRef") long apiRef,
                                                   @Param("clientId") long clientId);

    List<CarmenFreqConfig> getListValueByApi(@Param("apiRef") long apiRef);
}