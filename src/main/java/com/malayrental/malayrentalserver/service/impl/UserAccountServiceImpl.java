package com.malayrental.malayrentalserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.malayrental.malayrentalserver.common.IdGeneratorUtil;
import com.malayrental.malayrentalserver.dao.UserAccountMapper;
import com.malayrental.malayrentalserver.pojo.UserAccount;
import com.malayrental.malayrentalserver.service.UserAccountService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountMapper userAccountMapper;

    public UserAccountServiceImpl(UserAccountMapper userAccountMapper) {
        this.userAccountMapper = userAccountMapper;
    }

    @Override
    public boolean register(UserAccount user) {
        return userAccountMapper.insert(user) > 0;
    }

    @Override
    public UserAccount login(String phoneNumber, String password, String ip) {
        QueryWrapper<UserAccount> wrapper = new QueryWrapper<>();
        wrapper.eq("phone_number", phoneNumber).eq("password", password);
        UserAccount user = userAccountMapper.selectOne(wrapper);
        if (user != null) {
            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp(ip);
            userAccountMapper.updateById(user);
        }
        return user;
    }

    @Override
    public boolean existsByPhone(String phoneNumber) {
        QueryWrapper<UserAccount> wrapper = new QueryWrapper<>();
        wrapper.eq("phone_number", phoneNumber);
        return userAccountMapper.selectCount(wrapper) > 0;
    }

    @Override
    public String generateId(String prefix) {
        return IdGeneratorUtil.generateId(userAccountMapper, "user_id", prefix);
    }
}