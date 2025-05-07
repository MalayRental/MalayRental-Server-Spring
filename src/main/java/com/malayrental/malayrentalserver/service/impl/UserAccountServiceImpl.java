package com.malayrental.malayrentalserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.malayrental.malayrentalserver.common.IdGeneratorUtil;
import com.malayrental.malayrentalserver.dao.UserAccountMapper;
import com.malayrental.malayrentalserver.pojo.UserAccount;
import com.malayrental.malayrentalserver.service.UserAccountService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

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

    @Override
    public int deleteUser(String runUserId, String userId) {
        if (runUserId == null || userId == null) {
            return 3; // 参数不合法
        }
        try {
            UserAccount runUser = userAccountMapper.selectById(runUserId);
            if (runUser == null || !"Admin".equals(runUser.getUserRole())) {
                return 2; // 操作不合法
            }
            UserAccount targetUser = userAccountMapper.selectById(userId);
            if (targetUser == null) {
                return 1; // 目标账号不存在
            }
            int rows = userAccountMapper.deleteById(userId);
            return rows > 0 ? 0 : 4; // 0-成功，4-系统错误
        } catch (Exception e) {
            return 4; // 系统错误
        }
    }

    @Override
    public int registerUser(String userName, String phoneNumber, String avatar, String password) {
        if (userName == null || phoneNumber == null || avatar == null || password == null) {
            return 2; // 参数不合法
        }
        try {
            if (existsByPhone(phoneNumber)) {
                return 1; // 账号已存在
            }
            UserAccount user = new UserAccount();
            user.setUserId(generateId("U"));
            user.setUserName(userName);
            user.setPhoneNumber(phoneNumber);
            user.setAvatar(avatar);
            user.setPassword(password);
            user.setUserRole("User");
            user.setStatus("Normal");
            user.setCreateTime(LocalDateTime.now());
            // 注册时不设置登录时间和登录IP
            return register(user) ? 0 : 3;
        } catch (Exception e) {
            return 3; // 系统错误
        }
    }

    @Override
    public int loginUser(String phoneNumber, String password, String ip, UserAccount[] userHolder) {
        if (phoneNumber == null || password == null) {
            return 2; // 参数不合法
        }
        try {
            UserAccount user = login(phoneNumber, password, ip);
            if (user == null) {
                return 1; // 账号或密码错误
            }
            if ("Ban".equals(user.getStatus())) {
                return 3; // 账号状态异常
            }
            userHolder[0] = user;
            return 0; // 成功
        } catch (Exception e) {
            return 4; // 系统错误
        }
    }

    @Override
    public int updateUser(Map<String, Object> data) {
        if (data == null || data.get("userId") == null) {
            return 3; // 参数不合法
        }
        String userId = data.get("userId").toString();
        try {
            UserAccount user = userAccountMapper.selectById(userId);
            if (user == null) {
                return 1; // 账号不存在
            }
            // 检查是否要更新phoneNumber或password
            boolean needAdmin = data.containsKey("phoneNumber") || data.containsKey("password");
            if (needAdmin) {
                Object runUserObj = data.get("runUser");
                if (runUserObj == null) {
                    return 3; // 参数不合法
                }
                String runUserId = runUserObj.toString();
                UserAccount runUser = userAccountMapper.selectById(runUserId);
                if (runUser == null || !"Admin".equals(runUser.getUserRole())) {
                    return 2; // 操作不合法
                }
            }
            // 只允许更新userName, phoneNumber, password, avatar
            if (data.containsKey("userName")) {
                user.setUserName(data.get("userName").toString());
            }
            if (data.containsKey("phoneNumber")) {
                user.setPhoneNumber(data.get("phoneNumber").toString());
            }
            if (data.containsKey("password")) {
                user.setPassword(data.get("password").toString());
            }
            if (data.containsKey("avatar")) {
                user.setAvatar(data.get("avatar").toString());
            }
            int rows = userAccountMapper.updateById(user);
            return rows > 0 ? 0 : 4;
        } catch (Exception e) {
            return 4; // 系统错误
        }
    }
}