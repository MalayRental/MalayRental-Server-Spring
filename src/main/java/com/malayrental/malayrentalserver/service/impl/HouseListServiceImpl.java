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
                || data.get("proportion") == null || data.get("coverImage") == null) {
            return 1; // 参数不合法
        }
        String runUserId = data.get("runUser").toString();
        String houseName = data.get("houseName").toString();
        String area = data.get("area").toString();
        String orientation = data.get("orientation").toString();
        String proportionStr = data.get("proportion").toString();
        String coverImage = data.get("coverImage").toString();
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
} 