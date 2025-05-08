package com.malayrental.malayrentalserver.controller;

import com.malayrental.malayrentalserver.common.ApiResponse;
import com.malayrental.malayrentalserver.service.HouseAreaService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@SuppressWarnings("unchecked")
@RestController
@RequestMapping("/api/area")
public class HouseAreaController {
    private final HouseAreaService houseAreaService;

    public HouseAreaController(HouseAreaService houseAreaService) {
        this.houseAreaService = houseAreaService;
    }

    @PostMapping("/createArea")
    public ApiResponse createArea(@RequestBody Map<String, Object> req) {
        try {
            Object dataObj = req.get("data");
            if (!(dataObj instanceof Map)) {
                return ApiResponse.error(400, "参数不合法");
            }
            Map<String, Object> data = (Map<String, Object>) dataObj;
            int result = houseAreaService.createArea(data);
            return switch (result) {
                case 0 -> ApiResponse.ok("区域创建成功", null);
                case 1 -> ApiResponse.error(400, "参数不合法");
                case 2 -> ApiResponse.error(400, "操作不合法");
                case 3 -> ApiResponse.error(400, "区域已存在");
                default -> ApiResponse.error(500, "系统错误请稍后再试");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }
} 