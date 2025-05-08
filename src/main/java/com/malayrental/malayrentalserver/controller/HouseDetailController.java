package com.malayrental.malayrentalserver.controller;

import com.malayrental.malayrentalserver.common.ApiResponse;
import com.malayrental.malayrentalserver.service.HouseDetailService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/house")
public class HouseDetailController {
    private final HouseDetailService houseDetailService;

    public HouseDetailController(HouseDetailService houseDetailService) {
        this.houseDetailService = houseDetailService;
    }

    @PostMapping("/createHouseDetail")
    public ApiResponse createHouseDetail(@RequestBody Map<String, Object> req) {
        try {
            Object dataObj = req.get("data");
            if (!(dataObj instanceof Map)) {
                return ApiResponse.error(400, "参数不合法");
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) dataObj;
            int result = houseDetailService.createHouseDetail(data);
            return switch (result) {
                case 0 -> ApiResponse.ok("添加房源项详情成功", null);
                case 1 -> ApiResponse.error(400, "参数不合法");
                case 2 -> ApiResponse.error(400, "操作不合法");
                case 3 -> ApiResponse.error(400, "房源不存在");
                case 4 -> ApiResponse.error(400, "房源详情已存在");
                default -> ApiResponse.error(500, "系统错误请稍后再试");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }
} 