package com.zitech.gateway.apiconfig.service.imp;

import com.zitech.gateway.apiconfig.dao.gateway.ParamDAO;
import com.zitech.gateway.apiconfig.model.Param;
import com.zitech.gateway.apiconfig.service.ParamService;
import com.zitech.gateway.apiconfig.service.ServeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ParamServiceImpl implements ParamService {

    @Autowired
    private ParamDAO paramDAO;

    @Override
    public Param getByApiId(Integer apiId) {
        return paramDAO.selectByApiId(apiId);
    }

    @Override
    public List<Param> getAll() {
        return paramDAO.selectAll();
    }
}
