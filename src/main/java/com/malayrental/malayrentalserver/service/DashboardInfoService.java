package com.malayrental.malayrentalserver.service;

import java.util.Map;

public interface DashboardInfoService {
    /**
     * 获取dashboard总览信息
     * @param days 天数，获取最近days天的数据
     * @return Map<String, Map<String, String>> 以日期为key的统计数据
     */
    Map<String, Map<String, String>> getDashboardInfo(int days);
} 