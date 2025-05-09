package com.malayrental.malayrentalserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.malayrental.malayrentalserver.common.IdGeneratorUtil;
import com.malayrental.malayrentalserver.dao.UserFavoriteMapper;
import com.malayrental.malayrentalserver.dao.UserAccountMapper;
import com.malayrental.malayrentalserver.dao.HouseListMapper;
import com.malayrental.malayrentalserver.pojo.UserFavorite;
import com.malayrental.malayrentalserver.pojo.UserAccount;
import com.malayrental.malayrentalserver.pojo.HouseList;
import com.malayrental.malayrentalserver.service.UserFavoriteService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class UserFavoriteServiceImpl implements UserFavoriteService {
    private final UserFavoriteMapper userFavoriteMapper;
    private final UserAccountMapper userAccountMapper;
    private final HouseListMapper houseListMapper;

    public UserFavoriteServiceImpl(UserFavoriteMapper userFavoriteMapper, UserAccountMapper userAccountMapper, HouseListMapper houseListMapper) {
        this.userFavoriteMapper = userFavoriteMapper;
        this.userAccountMapper = userAccountMapper;
        this.houseListMapper = houseListMapper;
    }

    private boolean isRoleIllegal(UserAccount user) {
        if (user == null) return true;
        String role = user.getUserRole();
        return !("Admin".equals(role) || "Staff".equals(role) || "User".equals(role));
    }

    @Override
    public int addFavoriteItem(Map<String, Object> data) {
        if (data == null || data.get("runUser") == null || data.get("houseId") == null) {
            return 1; // 参数不合法
        }
        String runUserId = data.get("runUser").toString();
        String houseId = data.get("houseId").toString();
        try {
            UserAccount runUser = userAccountMapper.selectById(runUserId);
            if (isRoleIllegal(runUser)) {
                return 2; // 操作不合法
            }
            HouseList house = houseListMapper.selectById(houseId);
            if (house == null) {
                return 3; // 目标房源不存在
            }
            QueryWrapper<UserFavorite> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", runUserId).eq("house_id", houseId);
            if (userFavoriteMapper.selectCount(wrapper) > 0) {
                return 4; // 项目已收藏
            }
            UserFavorite favorite = new UserFavorite();
            favorite.setFavoriteId(IdGeneratorUtil.generateId(userFavoriteMapper, "favorite_id", "FAV"));
            favorite.setUserId(runUserId);
            favorite.setHouseId(houseId);
            favorite.setCreateTime(LocalDateTime.now());
            int rows = userFavoriteMapper.insert(favorite);
            return rows > 0 ? 0 : 5;
        } catch (Exception e) {
            return 5; // 系统错误
        }
    }

    @Override
    public int removeFavoriteItem(Map<String, Object> data) {
        if (data == null || data.get("runUser") == null || data.get("houseId") == null) {
            return 1; // 参数不合法
        }
        String runUserId = data.get("runUser").toString();
        String houseId = data.get("houseId").toString();
        try {
            UserAccount runUser = userAccountMapper.selectById(runUserId);
            if (isRoleIllegal(runUser)) {
                return 2; // 操作不合法
            }
            QueryWrapper<UserFavorite> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", runUserId).eq("house_id", houseId);
            UserFavorite favorite = userFavoriteMapper.selectOne(wrapper);
            if (favorite == null) {
                return 3; // 收藏项不存在
            }
            int rows = userFavoriteMapper.delete(wrapper);
            return rows > 0 ? 0 : 5;
        } catch (Exception e) {
            return 5; // 系统错误
        }
    }

    @Override
    public int getFavoriteList(Map<String, Object> data, List<Map<String, Object>> resultList) {
        if (data == null || data.get("runUser") == null) {
            return 1; // 参数不合法
        }
        String runUserId = data.get("runUser").toString();
        try {
            UserAccount runUser = userAccountMapper.selectById(runUserId);
            if (isRoleIllegal(runUser)) {
                return 2; // 操作不合法
            }
            QueryWrapper<UserFavorite> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", runUserId);
            List<UserFavorite> favorites = userFavoriteMapper.selectList(wrapper);
            for (UserFavorite favorite : favorites) {
                Map<String, Object> map = new HashMap<>();
                map.put("favoriteId", favorite.getFavoriteId());
                HouseList house = houseListMapper.selectById(favorite.getHouseId());
                if (house != null) {
                    map.put("houseInfo", buildHouseInfo(house));
                }
                resultList.add(map);
            }
            return 0;
        } catch (Exception e) {
            return 5; // 系统错误
        }
    }

    @Override
    public int checkFavoriteStatus(Map<String, Object> data, Map<String, Object> result) {
        if (data == null || data.get("runUser") == null || data.get("houseId") == null) {
            return 1; // 参数不合法
        }
        String runUserId = data.get("runUser").toString();
        String houseId = data.get("houseId").toString();
        try {
            UserAccount runUser = userAccountMapper.selectById(runUserId);
            if (isRoleIllegal(runUser)) {
                return 2; // 操作不合法
            }
            QueryWrapper<UserFavorite> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", runUserId).eq("house_id", houseId);
            boolean exists = userFavoriteMapper.selectCount(wrapper) > 0;
            result.put("status", exists ? "true" : "false");
            return 0;
        } catch (Exception e) {
            return 5; // 系统错误
        }
    }

    private Map<String, Object> buildHouseInfo(HouseList house) {
        Map<String, Object> houseInfo = new HashMap<>();
        houseInfo.put("houseId", house.getHouseId());
        houseInfo.put("houseName", house.getHouseName());
        houseInfo.put("area", house.getArea());
        houseInfo.put("orientation", house.getOrientation());
        houseInfo.put("proportion", house.getProportion());
        houseInfo.put("coverImage", house.getCoverImage());
        houseInfo.put("createUser", house.getCreateUser());
        houseInfo.put("createTime", house.getCreateTime());
        houseInfo.put("updateTime", house.getUpdateTime());
        houseInfo.put("status", house.getStatus());
        return houseInfo;
    }
} 