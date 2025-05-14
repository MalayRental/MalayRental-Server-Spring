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

    @PostMapping("/deleteArea")
    public ApiResponse deleteArea(@RequestBody Map<String, Object> req) {
        try {
            Object dataObj = req.get("data");
            if (!(dataObj instanceof Map)) {
                return ApiResponse.error(400, "参数不合法");
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) dataObj;
            int result = houseAreaService.deleteArea(data);
            return switch (result) {
                case 0 -> ApiResponse.ok("区域已删除", null);
                case 1 -> ApiResponse.error(400, "参数不合法");
                case 2 -> ApiResponse.error(400, "操作不合法");
                case 3 -> ApiResponse.error(400, "区域不存在");
                default -> ApiResponse.error(500, "系统错误请稍后再试");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }

    @PostMapping("/updateArea")
    public ApiResponse updateArea(@RequestBody Map<String, Object> req) {
        try {
            Object dataObj = req.get("data");
            if (!(dataObj instanceof Map)) {
                return ApiResponse.error(400, "参数不合法");
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) dataObj;
            int result = houseAreaService.updateArea(data);
            return switch (result) {
                case 0 -> ApiResponse.ok("区域信息更新成功", null);
                case 1 -> ApiResponse.error(400, "参数不合法");
                case 2 -> ApiResponse.error(400, "操作不合法");
                case 3 -> ApiResponse.error(400, "区域不存在");
                default -> ApiResponse.error(500, "系统错误请稍后再试");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }

    @PostMapping("/getAreaList")
    public ApiResponse getAreaList(@RequestBody Map<String, Object> req) {
        try {
            Object dataObj = req.get("data");
            Map<String, Object> data = null;
            if (dataObj instanceof Map) {
                data = (Map<String, Object>) dataObj;
            }
            java.util.List<Map<String, Object>> resultList = new java.util.ArrayList<>();
            int result = houseAreaService.getAreaList(data, resultList);
            if (result == 0) {
                return ApiResponse.ok("区域信息获取成功", resultList);
            } else {
                return ApiResponse.error(500, "系统错误请稍后再试");
            }
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }

    @PostMapping("/getAreaDetail")
    public ApiResponse getAreaDetail(@RequestBody Map<String, Object> req) {
        try {
            Object dataObj = req.get("data");
            if (!(dataObj instanceof Map)) {
                return ApiResponse.error(400, "参数不合法");
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) dataObj;
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            int code = houseAreaService.getAreaDetail(data, result);
            return switch (code) {
                case 0 -> ApiResponse.ok("区域信息获取成功", result);
                case 1 -> ApiResponse.error(400, "参数不合法");
                case 2 -> ApiResponse.error(400, "操作不合法");
                case 3 -> ApiResponse.error(400, "区域不存在");
                default -> ApiResponse.error(500, "系统错误请稍后再试");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }
} 