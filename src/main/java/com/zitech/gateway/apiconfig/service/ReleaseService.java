package com.zitech.gateway.apiconfig.service;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by panxl on 2016/5/4.
 */
public interface ReleaseService {
    JSONObject getDownloadInfo(String id, byte toEnv);

    JSONObject loadUploadFile(JSONObject jsonObject);
}
