package com.malayrental.malayrentalserver.controller;

import com.malayrental.malayrentalserver.common.ApiResponse;
import com.malayrental.malayrentalserver.service.DashboardInfoService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/dbinfo")
public class DashboardInfoController {
    private final DashboardInfoService dashboardInfoService;

    public DashboardInfoController(DashboardInfoService dashboardInfoService) {
        this.dashboardInfoService = dashboardInfoService;
    }

    @PostMapping("/getDashboardInfo")
    public ApiResponse getDashboardInfo(@RequestBody Map<String, Object> req) {
        // 默认获取最近7天
        int days = 7;
        try {
            Map<String, Map<String, String>> data = dashboardInfoService.getDashboardInfo(days);
            return ApiResponse.ok("获取总览信息成功", data);
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }
} 