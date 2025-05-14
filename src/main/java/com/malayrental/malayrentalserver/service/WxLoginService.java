package com.malayrental.malayrentalserver.service;

import com.malayrental.malayrentalserver.pojo.UserAccount;

import java.util.Map;

public interface WxLoginService {
    int wxLogin(String code, UserAccount[] userHolder, String[] openId);
} 