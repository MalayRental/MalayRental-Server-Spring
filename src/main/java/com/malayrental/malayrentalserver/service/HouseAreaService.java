package com.malayrental.malayrentalserver.service;

import java.util.Map;

public interface HouseAreaService {
    /**
     * 创建区域，data为请求data字段，返回：
     * 0-成功，1-参数不合法，2-操作不合法，3-区域已存在，4-系统错误
     */
    int createArea(Map<String, Object> data);
} 