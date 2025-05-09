package com.malayrental.malayrentalserver.controller;

import com.malayrental.malayrentalserver.common.ApiResponse;
import com.malayrental.malayrentalserver.service.UserFavoriteService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/api/favorite")
public class UserFavoriteController {
    private final UserFavoriteService userFavoriteService;

    public UserFavoriteController(UserFavoriteService userFavoriteService) {
        this.userFavoriteService = userFavoriteService;
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

    @PostMapping("/addFavoriteItem")
    public ApiResponse addFavoriteItem(@RequestBody Map<String, Object> req) {
        Map<String, Object> data = parseDataMap(req);
        if (data == null) {
            return ApiResponse.error(400, "参数不合法");
        }
        try {
            int result = userFavoriteService.addFavoriteItem(data);
            return switch (result) {
                case 0 -> ApiResponse.ok("收藏项添加成功", null);
                case 1 -> ApiResponse.error(400, "参数不合法");
                case 2 -> ApiResponse.error(400, "操作不合法");
                case 3 -> ApiResponse.error(400, "目标房源不存在");
                case 4 -> ApiResponse.error(400, "项目已收藏，无需重复操作");
                default -> ApiResponse.error(500, "系统错误请稍后再试");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }

    @PostMapping("/removeFavoriteItem")
    public ApiResponse removeFavoriteItem(@RequestBody Map<String, Object> req) {
        Map<String, Object> data = parseDataMap(req);
        if (data == null) {
            return ApiResponse.error(400, "参数不合法");
        }
        try {
            int result = userFavoriteService.removeFavoriteItem(data);
            return switch (result) {
                case 0 -> ApiResponse.ok("移除收藏项成功", null);
                case 1 -> ApiResponse.error(400, "参数不合法");
                case 2 -> ApiResponse.error(400, "操作不合法");
                case 3 -> ApiResponse.error(400, "收藏项不存在");
                default -> ApiResponse.error(500, "系统错误请稍后再试");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }

    @PostMapping("/getFavoriteList")
    public ApiResponse getFavoriteList(@RequestBody Map<String, Object> req) {
        Map<String, Object> data = parseDataMap(req);
        if (data == null) {
            return ApiResponse.error(400, "参数不合法");
        }
        try {
            List<Map<String, Object>> resultList = new ArrayList<>();
            int result = userFavoriteService.getFavoriteList(data, resultList);
            return switch (result) {
                case 0 -> ApiResponse.ok("获取收藏项列表成功", resultList);
                case 1 -> ApiResponse.error(400, "参数不合法");
                case 2 -> ApiResponse.error(400, "操作不合法");
                default -> ApiResponse.error(500, "系统错误请稍后再试");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }

    @PostMapping("/checkFavoriteStatus")
    public ApiResponse checkFavoriteStatus(@RequestBody Map<String, Object> req) {
        Map<String, Object> data = parseDataMap(req);
        if (data == null) {
            return ApiResponse.error(400, "参数不合法");
        }
        try {
            Map<String, Object> result = new HashMap<>();
            int code = userFavoriteService.checkFavoriteStatus(data, result);
            return switch (code) {
                case 0 -> ApiResponse.ok("检查收藏状态", result);
                case 1 -> ApiResponse.error(400, "参数不合法");
                case 2 -> ApiResponse.error(400, "操作不合法");
                default -> ApiResponse.error(500, "系统错误请稍后再试");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }
} 