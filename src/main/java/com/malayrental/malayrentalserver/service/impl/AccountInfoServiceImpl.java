package com.malayrental.malayrentalserver.service.impl;

import com.malayrental.malayrentalserver.dao.AccountInfoMapper;
import com.malayrental.malayrentalserver.dao.UserAccountMapper;
import com.malayrental.malayrentalserver.pojo.AccountInfo;
import com.malayrental.malayrentalserver.pojo.UserAccount;
import com.malayrental.malayrentalserver.service.AccountInfoService;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class AccountInfoServiceImpl implements AccountInfoService {
    private final AccountInfoMapper accountInfoMapper;
    private final UserAccountMapper userAccountMapper;

    public AccountInfoServiceImpl(AccountInfoMapper accountInfoMapper, UserAccountMapper userAccountMapper) {
        this.accountInfoMapper = accountInfoMapper;
        this.userAccountMapper = userAccountMapper;
    }

    @Override
    public int getAccountInfo(Map<String, Object> data, Map<String, Object> result) {
        if (data == null || data.get("runUser") == null || data.get("userId") == null) {
            return 1; // 参数不合法
        }
        String runUser = data.get("runUser").toString();
        String userId = data.get("userId").toString();
        try {
            if (!runUser.equals(userId)) {
                UserAccount runUserAccount = userAccountMapper.selectById(runUser);
                if (runUserAccount == null || runUserAccount.getUserRole() == null ||
                        !("Admin".equals(runUserAccount.getUserRole()) || "Staff".equals(runUserAccount.getUserRole()))) {
                    return 2; // 操作不合法
                }
            }
            UserAccount user = userAccountMapper.selectById(userId);
            if (user == null) {
                return 3; // 用户不存在
            }
            AccountInfo info = accountInfoMapper.selectById(userId);
            result.put("userId", user.getUserId());
            result.put("avatar", user.getAvatar());
            result.put("userName", user.getUserName());
            result.put("phoneNumber", user.getPhoneNumber());
            if (info != null) {
                result.put("fullName", info.getFullName());
                result.put("gender", info.getGender());
                result.put("age", info.getAge());
                result.put("email", info.getEmail());
                result.put("school", info.getSchool());
                result.put("bio", info.getBio());
            } else {
                result.put("fullName", null);
                result.put("gender", null);
                result.put("age", null);
                result.put("email", null);
                result.put("school", null);
                result.put("bio", null);
            }
            return 0;
        } catch (Exception e) {
            return 4; // 系统错误
        }
    }

    @Override
    public int updateAccountInfo(Map<String, Object> data) {
        if (data == null || data.get("runUser") == null || data.get("userId") == null) {
            return 1; // 参数不合法
        }
        String runUser = data.get("runUser").toString();
        String userId = data.get("userId").toString();
        try {
            if (!runUser.equals(userId)) {
                UserAccount runUserAccount = userAccountMapper.selectById(runUser);
                if (runUserAccount == null || runUserAccount.getUserRole() == null ||
                        !("Admin".equals(runUserAccount.getUserRole()) || "Staff".equals(runUserAccount.getUserRole()))) {
                    return 2; // 操作不合法
                }
            }
            // 检查userId是否存在
            UserAccount user = userAccountMapper.selectById(userId);
            if (user == null) {
                return 3; // 用户不存在
            }
            AccountInfo info = accountInfoMapper.selectById(userId);
            if (info == null) {
                info = new AccountInfo();
                info.setUserId(userId);
            }
            if (data.get("fullName") != null) info.setFullName(data.get("fullName").toString());
            if (data.get("gender") != null) info.setGender(data.get("gender").toString());
            if (data.get("age") != null) info.setAge(Integer.valueOf(data.get("age").toString()));
            if (data.get("email") != null) info.setEmail(data.get("email").toString());
            if (data.get("school") != null) info.setSchool(data.get("school").toString());
            if (data.get("bio") != null) info.setBio(data.get("bio").toString());
            if (accountInfoMapper.selectById(userId) == null) {
                accountInfoMapper.insert(info);
            } else {
                accountInfoMapper.updateById(info);
            }
            return 0;
        } catch (Exception e) {
            return 4; // 系统错误
        }
    }
} 