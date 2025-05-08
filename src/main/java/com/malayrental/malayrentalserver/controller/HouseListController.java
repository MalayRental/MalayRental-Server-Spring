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

    @PostMapping("/createHouseItem")
    public ApiResponse createHouseItem(@RequestBody Map<String, Object> req) {
        try {
            Object dataObj = req.get("data");
            if (!(dataObj instanceof Map)) {
                return ApiResponse.error(400, "参数不合法");
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) dataObj;
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
} 