package com.malayrental.malayrentalserver.service.impl;

import com.malayrental.malayrentalserver.dao.HouseDetailMapper;
import com.malayrental.malayrentalserver.dao.HouseListMapper;
import com.malayrental.malayrentalserver.dao.UserAccountMapper;
import com.malayrental.malayrentalserver.pojo.HouseDetail;
import com.malayrental.malayrentalserver.pojo.HouseList;
import com.malayrental.malayrentalserver.pojo.UserAccount;
import com.malayrental.malayrentalserver.service.HouseDetailService;
import com.malayrental.malayrentalserver.service.UserFavoriteService;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class HouseDetailServiceImpl implements HouseDetailService {
    private final HouseDetailMapper houseDetailMapper;
    private final HouseListMapper houseListMapper;
    private final UserAccountMapper userAccountMapper;
    private final UserFavoriteService userFavoriteService;

    public HouseDetailServiceImpl(HouseDetailMapper houseDetailMapper, HouseListMapper houseListMapper, UserAccountMapper userAccountMapper, UserFavoriteService userFavoriteService) {
        this.houseDetailMapper = houseDetailMapper;
        this.houseListMapper = houseListMapper;
        this.userAccountMapper = userAccountMapper;
        this.userFavoriteService = userFavoriteService;
    }

    @Override
    public int createHouseDetail(Map<String, Object> data) {
        if (data == null || data.get("runUser") == null || data.get("houseId") == null
                || data.get("address") == null || data.get("latLng") == null
                || data.get("desc") == null || data.get("tags") == null
                || data.get("detailImages") == null || data.get("floor") == null
                || data.get("availableDate") == null || data.get("paymentMethods") == null
                || data.get("agencyFees") == null || data.get("deposit") == null
                || data.get("facility") == null || data.get("community") == null) {
            return 1; // 参数不合法
        }
        String runUserId = data.get("runUser").toString();
        String houseId = data.get("houseId").toString();
        try {
            UserAccount runUser = userAccountMapper.selectById(runUserId);
            if (runUser == null ||
                !( "Admin".equals(runUser.getUserRole()) || "Staff".equals(runUser.getUserRole()) )) {
                return 2; // 操作不合法
            }
            // 检查houseId是否存在
            HouseList house = houseListMapper.selectById(houseId);
            if (house == null) {
                return 3; // 房源不存在
            }
            // 检查详情是否已存在
            if (houseDetailMapper.selectById(houseId) != null) {
                return 4; // 房源详情已存在
            }
            HouseDetail detail = new HouseDetail();
            detail.setHouseId(houseId);
            detail.setAddress(data.get("address").toString());
            detail.setLatLng(data.get("latLng").toString());
            detail.setDesc(data.get("desc").toString());
            detail.setTags(data.get("tags").toString());
            detail.setDetailImages(data.get("detailImages").toString());
            detail.setFloor(data.get("floor").toString());
            // 日期格式转换
            try {
                java.sql.Date date = java.sql.Date.valueOf(
                    data.get("availableDate").toString().replace("年", "-").replace("月", "-").replace("日", "")
                );
                detail.setAvailableDate(date);
            } catch (Exception e) {
                return 1; // 参数不合法（日期格式错误）
            }
            detail.setPaymentMethods(data.get("paymentMethods").toString());
            detail.setAgencyFees(new java.math.BigDecimal(data.get("agencyFees").toString()));
            detail.setDeposit(new java.math.BigDecimal(data.get("deposit").toString()));
            detail.setFacility(data.get("facility").toString());
            detail.setCommunity(data.get("community").toString());
            detail.setCreateTime(java.time.LocalDateTime.now());
            detail.setUpdateTime(java.time.LocalDateTime.now());
            int rows = houseDetailMapper.insert(detail);
            return rows > 0 ? 0 : 5; // 0-成功, 5-系统错误
        } catch (Exception e) {
            return 5; // 系统错误
        }
    }

    @Override
    public int getHouseDetail(Map<String, Object> data, java.util.Map<String, Object> result) {
        if (data == null || data.get("runUser") == null || data.get("houseId") == null) {
            return 1; // 参数不合法
        }
        String runUserId = data.get("runUser").toString();
        String houseId = data.get("houseId").toString();
        try {
            UserAccount runUser = userAccountMapper.selectById(runUserId);
            if (runUser == null || runUser.getUserRole() == null ||
                !("Admin".equals(runUser.getUserRole()) || "Staff".equals(runUser.getUserRole()) || "User".equals(runUser.getUserRole()))) {
                return 2; // 操作不合法
            }
            HouseDetail detail = houseDetailMapper.selectById(houseId);
            if (detail == null) {
                return 3; // 房源信息不存在
            }
            result.put("houseId", detail.getHouseId());
            result.put("address", detail.getAddress());
            result.put("latLng", detail.getLatLng());
            result.put("desc", detail.getDesc());
            result.put("tags", detail.getTags());
            result.put("detailImages", detail.getDetailImages());
            result.put("floor", detail.getFloor());
            result.put("availableDate", detail.getAvailableDate());
            result.put("paymentMethods", detail.getPaymentMethods());
            result.put("agencyFees", detail.getAgencyFees());
            result.put("deposit", detail.getDeposit());
            result.put("facility", detail.getFacility());
            result.put("community", detail.getCommunity());
            result.put("createTime", detail.getCreateTime());
            result.put("updateTime", detail.getUpdateTime());
            // 增加favoriteStatus字段
            java.util.Map<String, Object> favoriteResult = new java.util.HashMap<>();
            int favoriteCode = userFavoriteService.checkFavoriteStatus(data, favoriteResult);
            if (favoriteCode == 0) {
                // 规范：favoriteStatus为布尔类型
                result.put("favoriteStatus", "true".equals(favoriteResult.get("status")));
            } else {
                result.put("favoriteStatus", false);
            }
            return 0;
        } catch (Exception e) {
            return 5; // 系统错误
        }
    }

    @Override
    public int updateHouseDetail(Map<String, Object> data) {
        if (data == null || data.get("runUser") == null || data.get("houseId") == null) {
            return 1; // 参数不合法
        }
        String runUserId = data.get("runUser").toString();
        String houseId = data.get("houseId").toString();
        try {
            UserAccount runUser = userAccountMapper.selectById(runUserId);
            if (runUser == null || runUser.getUserRole() == null ||
                !("Admin".equals(runUser.getUserRole()) || "Staff".equals(runUser.getUserRole()))) {
                return 2; // 操作不合法
            }
            HouseDetail detail = houseDetailMapper.selectById(houseId);
            if (detail == null) {
                return 3; // 房源不存在
            }
            boolean updated = false;
            if (data.get("address") != null) {
                detail.setAddress(data.get("address").toString());
                updated = true;
            }
            if (data.get("latLng") != null) {
                detail.setLatLng(data.get("latLng").toString());
                updated = true;
            }
            if (data.get("desc") != null) {
                detail.setDesc(data.get("desc").toString());
                updated = true;
            }
            if (data.get("tags") != null) {
                detail.setTags(data.get("tags").toString());
                updated = true;
            }
            if (data.get("detailImages") != null) {
                detail.setDetailImages(data.get("detailImages").toString());
                updated = true;
            }
            if (data.get("floor") != null) {
                detail.setFloor(data.get("floor").toString());
                updated = true;
            }
            if (data.get("availableDate") != null) {
                try {
                    java.sql.Date date = java.sql.Date.valueOf(
                        data.get("availableDate").toString().replace("年", "-").replace("月", "-").replace("日", "")
                    );
                    detail.setAvailableDate(date);
                    updated = true;
                } catch (Exception e) {
                    return 1; // 日期格式错误
                }
            }
            if (data.get("paymentMethods") != null) {
                detail.setPaymentMethods(data.get("paymentMethods").toString());
                updated = true;
            }
            if (data.get("agencyFees") != null) {
                detail.setAgencyFees(new java.math.BigDecimal(data.get("agencyFees").toString()));
                updated = true;
            }
            if (data.get("deposit") != null) {
                detail.setDeposit(new java.math.BigDecimal(data.get("deposit").toString()));
                updated = true;
            }
            if (data.get("facility") != null) {
                detail.setFacility(data.get("facility").toString());
                updated = true;
            }
            if (data.get("community") != null) {
                detail.setCommunity(data.get("community").toString());
                updated = true;
            }
            if (!updated) {
                return 1; // 没有可更新字段
            }
            detail.setUpdateTime(java.time.LocalDateTime.now());
            int rows = houseDetailMapper.updateById(detail);
            return rows > 0 ? 0 : 5;
        } catch (Exception e) {
            return 5; // 系统错误
        }
    }
} 