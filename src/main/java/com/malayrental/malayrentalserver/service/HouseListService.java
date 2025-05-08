package com.malayrental.malayrentalserver.service;

import java.util.Map;

public interface HouseListService {
    int createHouseItem(Map<String, Object> data);
    int getHouseList(Map<String, Object> data, java.util.List<Map<String, Object>> resultList);
} 