package com.malayrental.malayrentalserver.controller;

import com.malayrental.malayrentalserver.common.ApiResponse;
import com.malayrental.malayrentalserver.pojo.UserAccount;
import com.malayrental.malayrentalserver.service.WxLoginService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class WxLoginController {

    private final WxLoginService wxLoginService;

    public WxLoginController(WxLoginService wxLoginService) {
        this.wxLoginService = wxLoginService;
    }

    private Map<String, Object> parseDataMap(Map<String, Object> req) {
        Object dataObj = req.get("data");
        if (!(dataObj instanceof Map)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) dataObj;
        return data;
    }

    @PostMapping("/wxLogin")
    public ApiResponse wxLogin(@RequestBody Map<String, Object> req) {
        Map<String, Object> data = parseDataMap(req);
        if (data == null) {
            return ApiResponse.error(400, "参数不合法");
        }

        try {
            String code = data.get("code") == null ? null : data.get("code").toString();
            if (code == null) {
                return ApiResponse.error(400, "缺少参数code");
            }

            UserAccount[] userHolder = new UserAccount[1];
            String[] openIdHolder = new String[1];
            int result = wxLoginService.wxLogin(code, userHolder, openIdHolder);

            return switch (result) {
                case 0 -> {
                    // 登录成功
                    UserAccount user = userHolder[0];
                    Map<String, Object> content = UserAccountController.createUserInfoMap(user, user.getTokenExpired());
                    yield ApiResponse.ok("微信登录成功", content);
                }
                case 1 -> {
                    // 需要注册
                    Map<String, Object> content = new HashMap<>();
                    content.put("openId", openIdHolder[0]);
                    yield new ApiResponse(201, "未绑定账号，需要进行注册", System.currentTimeMillis(), content);
                }
                case 2 -> ApiResponse.error(400, "微信服务器错误");
                default -> ApiResponse.error(500, "系统错误请稍后再试");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }
} 