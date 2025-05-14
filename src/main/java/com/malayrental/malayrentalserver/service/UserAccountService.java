package com.malayrental.malayrentalserver.service;

import com.malayrental.malayrentalserver.pojo.UserAccount;
import com.malayrental.malayrentalserver.security.TokenInfo;
import java.util.Map;

public interface UserAccountService {
    boolean register(UserAccount user);
    UserAccount login(String phoneNumber, String password, String ip);
    boolean existsByPhone(String phoneNumber);
    String generateId(String prefix);
    int deleteUser(String runUserId, String userId);
    int registerUser(String userName, String phoneNumber, String avatar, String password);
    int registerWxUser(String userName, String phoneNumber, String avatar, String password, String openId, UserAccount[] userHolder);
    int loginUser(String phoneNumber, String password, String ip, UserAccount[] userHolder);
    int updateUser(Map<String, Object> data);
    int banUser(Map<String, Object> data);
    int unbanUser(Map<String, Object> data);
    int getUserList(Map<String, Object> data, java.util.List<java.util.Map<String, Object>> resultList);
    int logout(Map<String, Object> data);
    void setAllUserOffline();
    TokenInfo generateUserToken(String userId);
    int autoLogin(String phoneNumber, String userToken, String ip, UserAccount[] userHolder);
    UserAccount getUserById(String userId);
    int checkUserPermission(String userId);
}