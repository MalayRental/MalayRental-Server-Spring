package com.malayrental.malayrentalserver.controller;

import com.malayrental.malayrentalserver.common.ApiResponse;
import com.malayrental.malayrentalserver.service.AccountInfoService;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class AccountInfoController {
    private final AccountInfoService accountInfoService;

    public AccountInfoController(AccountInfoService accountInfoService) {
        this.accountInfoService = accountInfoService;
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

    @PostMapping("/getAccountInfo")
    public ApiResponse getAccountInfo(@RequestBody Map<String, Object> req) {
        Map<String, Object> data = parseDataMap(req);
        if (data == null) {
            return ApiResponse.error(400, "参数不合法");
        }
        try {
            Map<String, Object> result = new HashMap<>();
            int code = accountInfoService.getAccountInfo(data, result);
            return switch (code) {
                case 0 -> ApiResponse.ok("用户详细资料获取成功", result);
                case 1 -> ApiResponse.error(400, "参数不合法");
                case 2 -> ApiResponse.error(400, "操作不合法");
                default -> ApiResponse.error(400, "系统错误请稍后再试！");
            };
        } catch (Exception e) {
            return ApiResponse.error(400, "系统错误请稍后再试！");
        }
    }

    @PostMapping("/updateAccountInfo")
    public ApiResponse updateAccountInfo(@RequestBody Map<String, Object> req) {
        Map<String, Object> data = parseDataMap(req);
        if (data == null) {
            return ApiResponse.error(400, "参数不合法");
        }
        try {
            int code = accountInfoService.updateAccountInfo(data);
            return switch (code) {
                case 0 -> ApiResponse.ok("用户详细资料更新成功", null);
                case 1 -> ApiResponse.error(400, "参数不合法");
                case 2 -> ApiResponse.error(400, "操作不合法");
                default -> ApiResponse.error(400, "系统错误请稍后再试！");
            };
        } catch (Exception e) {
            return ApiResponse.error(400, "系统错误请稍后再试！");
        }
    }
} 