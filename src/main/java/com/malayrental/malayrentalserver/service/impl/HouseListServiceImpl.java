package com.malayrental.malayrentalserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.malayrental.malayrentalserver.common.IdGeneratorUtil;
import com.malayrental.malayrentalserver.dao.HouseListMapper;
import com.malayrental.malayrentalserver.dao.UserAccountMapper;
import com.malayrental.malayrentalserver.pojo.HouseList;
import com.malayrental.malayrentalserver.pojo.UserAccount;
import com.malayrental.malayrentalserver.service.HouseListService;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class HouseListServiceImpl implements HouseListService {
    private final HouseListMapper houseListMapper;
    private final UserAccountMapper userAccountMapper;

    public HouseListServiceImpl(HouseListMapper houseListMapper, UserAccountMapper userAccountMapper) {
        this.houseListMapper = houseListMapper;
        this.userAccountMapper = userAccountMapper;
    }

    @Override
    public int createHouseItem(Map<String, Object> data) {
        if (data == null || data.get("runUser") == null || data.get("houseName") == null
                || data.get("area") == null || data.get("orientation") == null
                || data.get("proportion") == null || data.get("coverImage") == null
                || data.get("price") == null) {
            return 1; // 参数不合法
        }
        String runUserId = data.get("runUser").toString();
        String houseName = data.get("houseName").toString();
        String area = data.get("area").toString();
        String orientation = data.get("orientation").toString();
        String proportionStr = data.get("proportion").toString();
        String coverImage = data.get("coverImage").toString();
        String priceStr = data.get("price").toString();
        try {
            UserAccount runUser = userAccountMapper.selectById(runUserId);
            if (runUser == null || 
                !( "Admin".equals(runUser.getUserRole()) || "Staff".equals(runUser.getUserRole()) )) {
                return 2; // 操作不合法
            }
            QueryWrapper<HouseList> wrapper = new QueryWrapper<>();
            wrapper.eq("house_name", houseName);
            if (houseListMapper.selectCount(wrapper) > 0) {
                return 3; // 房源已存在
            }
            HouseList house = new HouseList();
            house.setHouseId(IdGeneratorUtil.generateId(houseListMapper, "house_id", "H"));
            house.setHouseName(houseName);
            house.setArea(area);
            house.setOrientation(orientation);
            house.setProportion(new java.math.BigDecimal(proportionStr));
            house.setCoverImage(coverImage);
            house.setPrice(new java.math.BigDecimal(priceStr));
            house.setCreateUser(runUserId);
            house.setCreateTime(java.time.LocalDateTime.now());
            house.setUpdateTime(java.time.LocalDateTime.now());
            if ("Admin".equals(runUser.getUserRole())) {
                house.setStatus("Normal");
            } else {
                house.setStatus("Pending");
            }
            int rows = houseListMapper.insert(house);
            return rows > 0 ? ("Admin".equals(runUser.getUserRole()) ? 0 : 4) : 5; // 0-Admin成功, 4-Staff成功, 5-系统错误
        } catch (Exception e) {
            return 5; // 系统错误
        }
    }

    @Override
    public int getHouseList(Map<String, Object> data, java.util.List<Map<String, Object>> resultList) {
        if (data == null || data.get("runUser") == null) {
            return 1; // 参数不合法
        }
        String runUserId = data.get("runUser").toString();
        try {
            UserAccount runUser = userAccountMapper.selectById(runUserId);
            if (runUser == null || runUser.getUserRole() == null ||
                !("Admin".equals(runUser.getUserRole()) || "Staff".equals(runUser.getUserRole()) || "User".equals(runUser.getUserRole()))) {
                return 2; // 操作不合法
            }
            java.util.List<HouseList> list = houseListMapper.selectList(null);
            for (HouseList house : list) {
                java.util.Map<String, Object> map = new java.util.HashMap<>();
                map.put("houseId", house.getHouseId());
                map.put("houseName", house.getHouseName());
                map.put("area", house.getArea());
                map.put("orientation", house.getOrientation());
                map.put("proportion", house.getProportion());
                map.put("coverImage", house.getCoverImage());
                map.put("price", house.getPrice());
                map.put("status", house.getStatus());
                map.put("createUser", house.getCreateUser());
                map.put("createTime", house.getCreateTime());
                map.put("updateTime", house.getUpdateTime());
                resultList.add(map);
            }
            return 0;
        } catch (Exception e) {
            return 5; // 系统错误
        }
    }

    @Override
    public int updateHouseItem(Map<String, Object> data) {
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
            HouseList house = houseListMapper.selectById(houseId);
            if (house == null) {
                return 3; // 房源不存在
            }
            boolean updated = false;
            if (data.get("houseName") != null) {
                house.setHouseName(data.get("houseName").toString());
                updated = true;
            }
            if (data.get("area") != null) {
                house.setArea(data.get("area").toString());
                updated = true;
            }
            if (data.get("orientation") != null) {
                house.setOrientation(data.get("orientation").toString());
                updated = true;
            }
            if (data.get("proportion") != null) {
                house.setProportion(new java.math.BigDecimal(data.get("proportion").toString()));
                updated = true;
            }
            if (data.get("coverImage") != null) {
                house.setCoverImage(data.get("coverImage").toString());
                updated = true;
            }
            if (!updated) {
                return 1; // 没有可更新字段
            }
            house.setUpdateTime(java.time.LocalDateTime.now());
            int rows = houseListMapper.updateById(house);
            return rows > 0 ? 0 : 5;
        } catch (Exception e) {
            return 5; // 系统错误
        }
    }

    public boolean setHouseStatus(String houseId, String status) {
        if (!"Normal".equals(status) && !"Rejected".equals(status)) {
            return false;
        }
        try {
            HouseList house = houseListMapper.selectById(houseId);
            if (house == null) return false;
            house.setStatus(status);
            house.setUpdateTime(java.time.LocalDateTime.now());
            return houseListMapper.updateById(house) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteHouse(String houseId) {
        try {
            return houseListMapper.deleteById(houseId) > 0;
        } catch (Exception e) {
            return false;
        }
    }
} 