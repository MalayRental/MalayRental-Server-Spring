package com.malayrental.malayrentalserver.service;

import java.util.Map;

public interface HouseDetailService {
    int createHouseDetail(Map<String, Object> data);
    int getHouseDetail(Map<String, Object> data, java.util.Map<String, Object> result);
    int updateHouseDetail(Map<String, Object> data);
} 