package com.malayrental.malayrentalserver.service.impl;

import com.malayrental.malayrentalserver.common.IdGeneratorUtil;
import com.malayrental.malayrentalserver.dao.UserBrowserHistoryMapper;
import com.malayrental.malayrentalserver.dao.HouseListMapper;
import com.malayrental.malayrentalserver.dao.UserAccountMapper;
import com.malayrental.malayrentalserver.pojo.UserBrowserHistory;
import com.malayrental.malayrentalserver.pojo.HouseList;
import com.malayrental.malayrentalserver.pojo.UserAccount;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.malayrental.malayrentalserver.service.UserBrowserHistoryService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class UserBrowserHistoryServiceImpl implements UserBrowserHistoryService {
    private final UserBrowserHistoryMapper userBrowserHistoryMapper;
    private final UserAccountMapper userAccountMapper;
    private final HouseListMapper houseListMapper;

    public UserBrowserHistoryServiceImpl(UserBrowserHistoryMapper userBrowserHistoryMapper, UserAccountMapper userAccountMapper, HouseListMapper houseListMapper) {
        this.userBrowserHistoryMapper = userBrowserHistoryMapper;
        this.userAccountMapper = userAccountMapper;
        this.houseListMapper = houseListMapper;
    }

    @Override
    public void addHistory(String userId, String houseId) {
        if (userId == null || houseId == null) {
            return; // 参数不合法
        }
        try {
            UserBrowserHistory history = new UserBrowserHistory();
            history.setHistoryId(IdGeneratorUtil.generateId(userBrowserHistoryMapper, "history_id", "HIS"));
            history.setUserId(userId);
            history.setHouseId(houseId);
            history.setCreateTime(LocalDateTime.now());
            userBrowserHistoryMapper.insert(history);
        } catch (Exception ignored) {
        }
    }

    @Override
    public int getHistoryList(Map<String, Object> data, List<Map<String, Object>> resultList) {
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
            QueryWrapper<UserBrowserHistory> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", runUserId);
            List<UserBrowserHistory> historyList = userBrowserHistoryMapper.selectList(wrapper);
            for (UserBrowserHistory history : historyList) {
                Map<String, Object> map = new HashMap<>();
                map.put("historyId", history.getHistoryId());
                HouseList house = houseListMapper.selectById(history.getHouseId());
                if (house != null) {
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
                    map.put("houseInfo", houseInfo);
                }
                resultList.add(map);
            }
            return 0;
        } catch (Exception e) {
            return 5; // 系统错误
        }
    }
} 