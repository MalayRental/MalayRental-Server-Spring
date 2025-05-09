package com.malayrental.malayrentalserver.service;

import java.util.List;
import java.util.Map;

public interface UserFavoriteService {
    int addFavoriteItem(Map<String, Object> data);
    int removeFavoriteItem(Map<String, Object> data);
    int getFavoriteList(Map<String, Object> data, List<Map<String, Object>> resultList);
    int checkFavoriteStatus(Map<String, Object> data, Map<String, Object> result);
} 