package com.malayrental.malayrentalserver.service;

import java.util.Map;
import java.util.List;

public interface HouseAreaService {
    /**
     * 创建区域，data为请求data字段，返回：
     * 0-成功，1-参数不合法，2-操作不合法，3-区域已存在，4-系统错误
     */
    int createArea(Map<String, Object> data);
    int deleteArea(Map<String, Object> data);
    int updateArea(Map<String, Object> data);
    int getAreaList(Map<String, Object> data, java.util.List<Map<String, Object>> resultList);
    int getAreaDetail(Map<String, Object> data, Map<String, Object> result);

    /**
     * 获取带有houseCount的区域信息列表，仅Admin可用
     * @param runUserId 操作用户ID
     * @param resultList 返回的区域信息列表
     * @return 0-成功，2-无权限，4-系统错误
     */
    int getAreaListWithAdminInfo(String runUserId, List<Map<String, Object>> resultList);
} 