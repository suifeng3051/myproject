package com.zitech.gateway.gateway.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.zitech.gateway.AppConfig;
import com.zitech.gateway.apiconfig.model.Group;
import com.zitech.gateway.apiconfig.model.Serve;
import com.zitech.gateway.apiconfig.service.GroupService;
import com.zitech.gateway.apiconfig.service.ServeService;
import com.zitech.gateway.gateway.exception.CacheException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Component
@LocalCache("group")
public class GroupCache implements ILocalCache {

    private static Logger logger = LoggerFactory.getLogger(GroupCache.class);

    @Autowired
    private GroupService groupService;

    private Cache<String, Group> cache = CacheBuilder.newBuilder().maximumSize(10000).build();

    public Group get(Integer groupId) {
        try {
            return cache.get(String.valueOf(groupId), () -> groupService.getById(groupId));
        } catch (ExecutionException e) {
            logger.info("get cache error:", e);
            throw new CacheException(5214, "非法Group");
        }
    }

    @Override
    public void load() {
        List<Group> groupList = groupService.getAll();
        for (Group group : groupList) {
            get(group.getId());
        }
    }

    public void clear() {
        cache.invalidateAll();
    }

    @Override
    public long cacheSize() {
        return cache.size();
    }
}
