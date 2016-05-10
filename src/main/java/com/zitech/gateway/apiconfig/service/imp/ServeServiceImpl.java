package com.zitech.gateway.apiconfig.service.imp;

import com.zitech.gateway.apiconfig.dao.gateway.ServeDAO;
import com.zitech.gateway.apiconfig.model.Serve;
import com.zitech.gateway.apiconfig.service.ServeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class ServeServiceImpl implements ServeService {

    @Autowired
    private ServeDAO serveDAO;

    @Override
    public Serve getByApiId(Integer apiId) {
        return serveDAO.selectByApiId(apiId);
    }

    @Override
    public List<Serve> getAll(Byte env) {
        return serveDAO.selectAll(env);
    }

    @Override
    public void deleteServeByApiId(Integer apiId, Integer userid) {
        Serve serve = serveDAO.selectByApiId(apiId);
        serve.setUpdatedId(userid);
        serve.setUpdatedTime(new Date());
        serve.setDeleted((byte) 1);
        serveDAO.updateByPrimaryKeySelective(serve);
    }

    @Override
    public void updateServe(Serve serve) {
        serveDAO.updateByPrimaryKeySelective(serve);
    }

    @Override
    public void deleteServeRealById(Integer id) {
        serveDAO.deleteByPrimaryKey(id);
    }
}
