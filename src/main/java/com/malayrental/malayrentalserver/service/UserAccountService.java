package com.malayrental.malayrentalserver.service;

import com.malayrental.malayrentalserver.pojo.UserAccount;

public interface UserAccountService {
    boolean register(UserAccount user);
    UserAccount login(String phoneNumber, String password, String ip);
    boolean existsByPhone(String phoneNumber);
    String generateId(String prefix);
    int deleteUser(String runUserId, String userId);
    int registerUser(String userName, String phoneNumber, String avatar, String password);
    int loginUser(String phoneNumber, String password, String ip, UserAccount[] userHolder);
}