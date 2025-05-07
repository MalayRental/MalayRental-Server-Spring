package com.malayrental.malayrentalserver.controller;

import com.malayrental.malayrentalserver.common.ApiResponse;
import com.malayrental.malayrentalserver.pojo.UserAccount;
import com.malayrental.malayrentalserver.service.UserAccountService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

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
            String userName = data.get("userName") == null ? null : data.get("userName").toString();
            String phone = data.get("phoneNumber") == null ? null : data.get("phoneNumber").toString();
            String avatar = data.get("avatar") == null ? null : data.get("avatar").toString();
            String password = data.get("password") == null ? null : data.get("password").toString();
            int result = userAccountService.registerUser(userName, phone, avatar, password);
            return switch (result) {
                case 0 -> ApiResponse.ok("注册成功，请登录", null);
                case 1 -> ApiResponse.error(400, "账号已存在");
                case 2 -> ApiResponse.error(400, "参数不合法");
                default -> ApiResponse.error(500, "系统错误请稍后再试！");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试！");
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
            String phone = data.get("phoneNumber") == null ? null : data.get("phoneNumber").toString();
            String password = data.get("password") == null ? null : data.get("password").toString();
            String ip = request.getRemoteAddr();
            UserAccount[] userHolder = new UserAccount[1];
            int result = userAccountService.loginUser(phone, password, ip, userHolder);
            return switch (result) {
                case 0 -> {
                    UserAccount user = userHolder[0];
                    Map<String, Object> content = new java.util.HashMap<>();
                    content.put("userId", user.getUserId());
                    content.put("userName", user.getUserName());
                    content.put("phoneNumber", user.getPhoneNumber());
                    content.put("avatar", user.getAvatar());
                    content.put("role", user.getUserRole());
                    yield ApiResponse.ok("登录成功", content);
                }
                case 1 -> ApiResponse.error(400, "账号或密码错误");
                case 2 -> ApiResponse.error(400, "参数不合法");
                case 3 -> ApiResponse.error(400, "账号状态异常，请联系客服");
                default -> ApiResponse.error(500, "系统错误请稍后再试！");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试！");
        }
    }

    @PostMapping("/deleteUser")
    public ApiResponse deleteUser(@RequestBody Map<String, Object> req) {
        try {
            Object dataObj = req.get("data");
            if (!(dataObj instanceof Map)) {
                return ApiResponse.error(400, "参数不合法");
            }
            Map<String, Object> data = (Map<String, Object>) dataObj;
            if (data.get("runUser") == null || data.get("userId") == null) {
                return ApiResponse.error(400, "参数不合法");
            }
            String runUserId = data.get("runUser").toString();
            String userId = data.get("userId").toString();
            int result = userAccountService.deleteUser(runUserId, userId);
            return switch (result) {
                case 0 -> ApiResponse.ok("删除成功", null);
                case 1 -> ApiResponse.error(400, "目标账号不存在");
                case 2 -> ApiResponse.error(400, "操作不合法");
                case 3 -> ApiResponse.error(400, "参数不合法");
                default -> ApiResponse.error(500, "系统错误请稍后再试！");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试！");
        }
    }

    @PostMapping("/updateUser")
    public ApiResponse updateUser(@RequestBody Map<String, Object> req) {
        try {
            Object dataObj = req.get("data");
            if (!(dataObj instanceof Map)) {
                return ApiResponse.error(400, "参数不合法");
            }
            Map<String, Object> data = (Map<String, Object>) dataObj;
            int result = userAccountService.updateUser(data);
            return switch (result) {
                case 0 -> ApiResponse.ok("更新成功", null);
                case 1 -> ApiResponse.error(400, "目标账号不存在");
                case 2 -> ApiResponse.error(400, "操作不合法");
                case 3 -> ApiResponse.error(400, "参数不合法");
                default -> ApiResponse.error(500, "系统错误请稍后再试！");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试！");
        }
    }

    @PostMapping("/banUser")
    public ApiResponse banUser(@RequestBody Map<String, Object> req) {
        try {
            Object dataObj = req.get("data");
            if (!(dataObj instanceof Map)) {
                return ApiResponse.error(400, "参数不合法");
            }
            Map<String, Object> data = (Map<String, Object>) dataObj;
            int result = userAccountService.banUser(data);
            return switch (result) {
                case 0 -> ApiResponse.ok("用户已封禁", null);
                case 1 -> ApiResponse.error(400, "目标账号不存在");
                case 2 -> ApiResponse.error(400, "操作不合法");
                case 3 -> ApiResponse.error(400, "参数不合法");
                default -> ApiResponse.error(500, "系统错误请稍后再试！");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试！");
        }
    }
}