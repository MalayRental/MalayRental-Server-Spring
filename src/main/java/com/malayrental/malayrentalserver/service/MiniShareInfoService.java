package com.malayrental.malayrentalserver.service;

import com.malayrental.malayrentalserver.pojo.MiniShareInfo;
import java.util.Map;

public interface MiniShareInfoService {
    MiniShareInfo getEnableShareInfoByPage(String page);
    int addShareInfo(Map<String, Object> data);
    int updateShareInfo(Map<String, Object> data);
} 