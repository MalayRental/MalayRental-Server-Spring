package com.malayrental.malayrentalserver.controller;

import com.malayrental.malayrentalserver.common.ApiResponse;
import com.malayrental.malayrentalserver.service.HouseListService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/house")
public class HouseListController {
    private final HouseListService houseListService;

    public HouseListController(HouseListService houseListService) {
        this.houseListService = houseListService;
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

    @PostMapping("/createHouseItem")
    public ApiResponse createHouseItem(@RequestBody Map<String, Object> req) {
        Map<String, Object> data = parseDataMap(req);
        if (data == null) {
            return ApiResponse.error(400, "参数不合法");
        }
        try {
            int result = houseListService.createHouseItem(data);
            return switch (result) {
                case 0 -> ApiResponse.ok("房源项发布成功", null);
                case 1 -> ApiResponse.error(400, "参数不合法");
                case 2 -> ApiResponse.error(400, "操作不合法");
                case 3 -> ApiResponse.error(400, "房源已存在");
                case 4 -> ApiResponse.ok("已创建房源发布审批，审批通过后方可显示", null);
                default -> ApiResponse.error(500, "系统错误请稍后再试");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }

    @PostMapping("/getHouseList")
    public ApiResponse getHouseList(@RequestBody Map<String, Object> req) {
        Map<String, Object> data = parseDataMap(req);
        if (data == null) {
            return ApiResponse.error(400, "参数不合法");
        }
        try {
            java.util.List<Map<String, Object>> resultList = new java.util.ArrayList<>();
            int result = houseListService.getHouseList(data, resultList);
            return switch (result) {
                case 0 -> ApiResponse.ok("房源列表获取成功", resultList);
                case 1 -> ApiResponse.error(400, "参数不合法");
                case 2 -> ApiResponse.error(400, "操作不合法");
                default -> ApiResponse.error(500, "系统错误请稍后再试");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }

    @PostMapping("/updateHouseItem")
    public ApiResponse updateHouseItem(@RequestBody Map<String, Object> req) {
        Map<String, Object> data = parseDataMap(req);
        if (data == null) {
            return ApiResponse.error(400, "参数不合法");
        }
        try {
            int result = houseListService.updateHouseItem(data);
            return switch (result) {
                case 0 -> ApiResponse.ok("更新房源项信息成功", null);
                case 1 -> ApiResponse.error(400, "参数不合法");
                case 2 -> ApiResponse.error(400, "操作不合法");
                case 3 -> ApiResponse.error(400, "房源不存在");
                default -> ApiResponse.error(500, "系统错误请稍后再试");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }
} 