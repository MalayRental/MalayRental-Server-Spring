package com.malayrental.malayrentalserver.controller;

import com.malayrental.malayrentalserver.common.ApiResponse;
import com.malayrental.malayrentalserver.service.UserBrowserHistoryService;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/history")
public class UserHistoryController {
    private final UserBrowserHistoryService userBrowserHistoryService;

    public UserHistoryController(UserBrowserHistoryService userBrowserHistoryService) {
        this.userBrowserHistoryService = userBrowserHistoryService;
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

    @PostMapping("/getHistoryList")
    public ApiResponse getHistoryList(@RequestBody Map<String, Object> req) {
        Map<String, Object> data = parseDataMap(req);
        if (data == null) {
            return ApiResponse.error(400, "参数不合法");
        }
        try {
            List<Map<String, Object>> resultList = new ArrayList<>();
            int code = userBrowserHistoryService.getHistoryList(data, resultList);
            return switch (code) {
                case 0 -> ApiResponse.ok("获取历史浏览列表成功", resultList);
                case 1 -> ApiResponse.error(400, "参数不合法");
                case 2 -> ApiResponse.error(400, "操作不合法");
                default -> ApiResponse.error(500, "系统错误请稍后再试");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }

    @PostMapping("/clearMyHistory")
    public ApiResponse clearMyHistory(@RequestBody Map<String, Object> req) {
        Map<String, Object> data = parseDataMap(req);
        if (data == null) {
            return ApiResponse.error(400, "参数不合法");
        }
        try {
            int code = userBrowserHistoryService.clearHistory(data);
            return switch (code) {
                case 0 -> ApiResponse.ok("清空浏览历史成功", null);
                case 1 -> ApiResponse.error(400, "参数不合法");
                case 2 -> ApiResponse.error(400, "操作不合法");
                default -> ApiResponse.error(500, "系统错误请稍后再试");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }
}
