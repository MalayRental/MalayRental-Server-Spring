package com.malayrental.malayrentalserver.service;

import com.malayrental.malayrentalserver.pojo.MiniSearchKey;
import java.util.List;
import java.util.Map;

public interface MiniSearchKeyService {
    List<MiniSearchKey> getEnableSearchKeys();
    int addSearchKey(Map<String, Object> data);
    int deleteSearchKey(Map<String, Object> data);
} 