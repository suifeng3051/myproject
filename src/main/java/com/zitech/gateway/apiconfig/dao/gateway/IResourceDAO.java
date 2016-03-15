package com.zitech.gateway.apiconfig.dao.gateway;


import com.zitech.gateway.apiconfig.dto.req.OpenResourceReq;
import com.zitech.gateway.apiconfig.model.OpenResource;
import com.zitech.platform.dao.base.func.IEntityDAO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IResourceDAO extends IEntityDAO<OpenResource, OpenResourceReq> {

    OpenResource getByUri(@Param("uri") String uri);

    OpenResource getByUriVersion(@Param("uri") String uri, @Param("version") String version);

    void deleteByUri(@Param("uri") String uri);

    List<String>getResourceUriList();

}