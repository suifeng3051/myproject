package com.zitech.gateway.apiconfig.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * Created by panxl on 2016/5/4.
 */
public interface ReleaseService {
    JSONObject getDownloadInfo(String id);
}
