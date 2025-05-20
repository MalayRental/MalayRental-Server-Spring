package com.malayrental.malayrentalserver.service.impl;

import com.malayrental.malayrentalserver.dao.*;
import com.malayrental.malayrentalserver.pojo.UserAccount;
import com.malayrental.malayrentalserver.pojo.HouseList;
import com.malayrental.malayrentalserver.pojo.MessageList;
import com.malayrental.malayrentalserver.pojo.HouseArea;
import com.malayrental.malayrentalserver.pojo.UserBrowserHistory;
import com.malayrental.malayrentalserver.service.DashboardInfoService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardInfoServiceImpl implements DashboardInfoService {
    private final UserAccountMapper userAccountMapper;
    private final HouseListMapper houseListMapper;
    private final MessageListMapper messageListMapper;
    private final HouseAreaMapper houseAreaMapper;
    private final UserBrowserHistoryMapper userBrowserHistoryMapper;

    public DashboardInfoServiceImpl(
        UserAccountMapper userAccountMapper,
        HouseListMapper houseListMapper,
        MessageListMapper messageListMapper,
        HouseAreaMapper houseAreaMapper,
        UserBrowserHistoryMapper userBrowserHistoryMapper
    ) {
        this.userAccountMapper = userAccountMapper;
        this.houseListMapper = houseListMapper;
        this.messageListMapper = messageListMapper;
        this.houseAreaMapper = houseAreaMapper;
        this.userBrowserHistoryMapper = userBrowserHistoryMapper;
    }

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Map<String, Map<String, String>> getDashboardInfo(int days) {
        Map<String, Map<String, String>> result = new HashMap<>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i < days; i++) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.format(DATE_FORMATTER);
            LocalDateTime endOfDay = date.atTime(23, 59, 59);
            Map<String, String> dayData = new HashMap<>();
            // 用户数
            Long userCount = userAccountMapper.selectCount(
                new QueryWrapper<UserAccount>()
                    .le("create_time", endOfDay)
            );
            // 房源数
            Long houseCount = houseListMapper.selectCount(
                new QueryWrapper<HouseList>()
                    .le("create_time", endOfDay)
            );
            // 消息数
            Long messageCount = messageListMapper.selectCount(
                new QueryWrapper<MessageList>()
                    .le("create_time", endOfDay)
            );
            // 区域数
            Long areaCount = houseAreaMapper.selectCount(
                new QueryWrapper<HouseArea>()
                    .le("create_time", endOfDay)
            );
            // 浏览历史数
            Long historyCount = userBrowserHistoryMapper.selectCount(
                new QueryWrapper<UserBrowserHistory>()
                    .le("create_time", endOfDay)
            );
            dayData.put("userCount", userCount.toString());
            dayData.put("houseCount", houseCount.toString());
            dayData.put("messageCount", messageCount.toString());
            dayData.put("areaCount", areaCount.toString());
            dayData.put("historyCount", historyCount.toString());
            result.put(dateStr, dayData);
        }
        return result;
    }
} 