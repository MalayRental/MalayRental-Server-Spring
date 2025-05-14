package com.malayrental.malayrentalserver.service;

import java.util.List;
import java.util.Map;
import com.malayrental.malayrentalserver.pojo.MiniBanner;

public interface MiniBannerService {
    /**
     * 创建Banner图
     * @param data 包含创建Banner所需数据
     * @return 0-成功，1-参数不合法，2-操作不合法，3-Banner图片不存在，5-系统错误
     */
    int createBanner(Map<String, Object> data);
    
    /**
     * 获取Banner图列表
     * @return 状态为Enable的Banner列表
     */
    List<MiniBanner> getBannerList();
    
    /**
     * 更新Banner状态
     * @param data 包含bannerId和status字段
     * @return 0-成功，1-参数不合法，2-操作不合法，3-Banner不存在，5-系统错误
     */
    int updateBannerStatus(Map<String, Object> data);
} 