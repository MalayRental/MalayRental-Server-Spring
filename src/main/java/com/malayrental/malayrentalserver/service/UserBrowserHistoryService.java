package com.malayrental.malayrentalserver.service;

public interface UserBrowserHistoryService {
    void addHistory(String userId, String houseId);
    int getHistoryList(java.util.Map<String, Object> data, java.util.List<java.util.Map<String, Object>> resultList);
} 