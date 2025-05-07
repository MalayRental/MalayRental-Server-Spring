package com.malayrental.malayrentalserver.controller;

import com.malayrental.malayrentalserver.common.ApiResponse;
import com.malayrental.malayrentalserver.pojo.UserAccount;
import com.malayrental.malayrentalserver.service.UserAccountService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@SuppressWarnings("unchecked")
@RestController
@RequestMapping("/api/user")
public class UserAccountController {

    private final UserAccountService userAccountService;

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PostMapping("/register")
    public ApiResponse register(@RequestBody Map<String, Object> req) {
        try {
            Object dataObj = req.get("data");
            if (!(dataObj instanceof Map)) {
                return ApiResponse.error(400, "参数不合法");
            }
            Map<String, Object> data = (Map<String, Object>) dataObj;
            if (data.get("userName") == null || data.get("phoneNumber") == null
                    || data.get("avatar") == null || data.get("password") == null) {
                return ApiResponse.error(400, "参数不合法");
            }
            String phone = data.get("phoneNumber").toString();
            if (userAccountService.existsByPhone(phone)) {
                return ApiResponse.error(400, "账号已存在");
            }
            UserAccount user = new UserAccount();
            user.setUserId(userAccountService.generateId("U"));
            user.setUserName(data.get("userName").toString());
            user.setPhoneNumber(phone);
            user.setAvatar(data.get("avatar").toString());
            user.setPassword(data.get("password").toString());
            user.setUserRole("User");
            user.setStatus("Normal");
            user.setCreateTime(LocalDateTime.now());
            boolean ok = userAccountService.register(user);
            if (ok) {
                return ApiResponse.ok("注册成功，请登录", null);
            } else {
                return ApiResponse.error(500, "系统错误请稍后再试");
            }
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }

    @PostMapping("/login")
    public ApiResponse login(@RequestBody Map<String, Object> req, HttpServletRequest request) {
        try {
            Object dataObj = req.get("data");
            if (!(dataObj instanceof Map)) {
                return ApiResponse.error(400, "参数不合法");
            }
            Map<String, Object> data = (Map<String, Object>) dataObj;
            if (data.get("phoneNumber") == null || data.get("password") == null) {
                return ApiResponse.error(400, "参数不合法");
            }
            String phone = data.get("phoneNumber").toString();
            String password = data.get("password").toString();
            String ip = request.getRemoteAddr();
            // 这里省略登录失败次数校验逻辑，后续可补充
            UserAccount user = userAccountService.login(phone, password, ip);
            if (user == null) {
                return ApiResponse.error(400, "账号或密码错误");
            }
            if ("Ban".equals(user.getStatus())) {
                return ApiResponse.error(400, "账号状态异常，请联系客服");
            }
            Map<String, Object> content = new java.util.HashMap<>();
            content.put("userId", user.getUserId());
            content.put("userName", user.getUserName());
            content.put("phoneNumber", user.getPhoneNumber());
            content.put("avatar", user.getAvatar());
            content.put("role", user.getUserRole());
            return ApiResponse.ok("登录成功", content);
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }
}