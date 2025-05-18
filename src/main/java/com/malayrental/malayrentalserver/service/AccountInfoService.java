package com.malayrental.malayrentalserver.service;

import java.util.Map;

public interface AccountInfoService {
    int getAccountInfo(Map<String, Object> data, Map<String, Object> result);
    int updateAccountInfo(Map<String, Object> data);
} 