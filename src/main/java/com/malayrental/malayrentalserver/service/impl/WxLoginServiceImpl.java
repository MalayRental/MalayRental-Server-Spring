package com.malayrental.malayrentalserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malayrental.malayrentalserver.dao.UserAccountMapper;
import com.malayrental.malayrentalserver.pojo.UserAccount;
import com.malayrental.malayrentalserver.service.UserAccountService;
import com.malayrental.malayrentalserver.service.WxLoginService;
import com.malayrental.malayrentalserver.security.TokenInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
public class WxLoginServiceImpl implements WxLoginService {

    private static final Logger logger = LoggerFactory.getLogger(WxLoginServiceImpl.class);

    @Value("${wechat.miniprogram.appid}")
    private String appid;

    @Value("${wechat.miniprogram.secret}")
    private String secret;

    private final UserAccountMapper userAccountMapper;
    private final UserAccountService userAccountService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public WxLoginServiceImpl(UserAccountMapper userAccountMapper, UserAccountService userAccountService) {
        this.userAccountMapper = userAccountMapper;
        this.userAccountService = userAccountService;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public int wxLogin(String code, UserAccount[] userHolder, String[] openIdHolder) {
        if (code == null) {
            return 2; // 参数错误
        }

        try {
            // 构建请求微信服务器的URL
            String url = "https://api.weixin.qq.com/sns/jscode2session" +
                    "?appid=" + appid +
                    "&secret=" + secret +
                    "&js_code=" + code +
                    "&grant_type=wqw051107";

            // 发送请求到微信服务器
            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);

            // 检查返回是否有错误
            if (jsonNode.has("errcode") && jsonNode.get("errcode").asInt() != 0) {
                return 2; // 微信服务器返回错误
            }

            // 获取openid
            String openId = jsonNode.get("openid").asText();
            openIdHolder[0] = openId;

            // 查询数据库中是否存在该openid
            QueryWrapper<UserAccount> wrapper = new QueryWrapper<>();
            wrapper.eq("open_id", openId);
            UserAccount user = userAccountMapper.selectOne(wrapper);

            if (user == null) {
                return 1; // 需要注册
            }

            // 存在则更新登录状态
            user.setLastLoginTime(LocalDateTime.now());
            user.setOnlineStatus("online");
            
            // 生成用户token
            TokenInfo tokenInfo = userAccountService.generateUserToken(user.getUserId());
            if (tokenInfo != null) {
                user.setUserToken(tokenInfo.token());
                user.setTokenExpired(tokenInfo.expired());
            }
            
            userAccountMapper.updateById(user);
            userHolder[0] = user;
            return 0; // 登录成功
        } catch (Exception e) {
            logger.error("微信小程序登录异常", e);
            return 3; // 系统错误
        }
    }
} 