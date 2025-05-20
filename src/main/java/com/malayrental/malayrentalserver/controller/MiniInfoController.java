package com.malayrental.malayrentalserver.controller;

import com.malayrental.malayrentalserver.common.ApiResponse;
import com.malayrental.malayrentalserver.pojo.MiniBanner;
import com.malayrental.malayrentalserver.service.MiniBannerService;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/miniInfo")
public class MiniInfoController {
    private final MiniBannerService miniBannerService;

    public MiniInfoController(MiniBannerService miniBannerService) {
        this.miniBannerService = miniBannerService;
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

    /**
     * 创建Banner图
     * 图片应先通过 /api/images/upload/banner 接口上传
     * 然后使用返回的文件名作为image参数
     */
    @PostMapping("/createBanner")
    public ApiResponse createBanner(@RequestBody Map<String, Object> req) {
        Map<String, Object> data = parseDataMap(req);
        if (data == null) {
            return ApiResponse.error(400, "参数不合法");
        }
        try {
            int result = miniBannerService.createBanner(data);
            return switch (result) {
                case 0 -> ApiResponse.ok("创建Banner图成功，请开启后使用", null);
                case 1 -> ApiResponse.error(400, "参数不合法");
                case 2 -> ApiResponse.error(400, "操作不合法");
                case 3 -> ApiResponse.error(400, "Banner图片不存在");
                default -> ApiResponse.error(500, "系统错误请稍后再试");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }

    @SuppressWarnings("unused")
    @PostMapping("/getBannerList")
    public ApiResponse getBannerList(@RequestBody Map<String, Object> req) {
        try {
            List<MiniBanner> bannerList = miniBannerService.getBannerList();
            
            if (bannerList == null) {
                return ApiResponse.error(500, "系统错误请稍后再试");
            }
            
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (MiniBanner banner : bannerList) {
                Map<String, Object> item = new HashMap<>();
                item.put("bannerId", banner.getBannerId());
                item.put("image", banner.getImage());
                item.put("link", banner.getLink());
                item.put("status", banner.getStatus());
                resultList.add(item);
            }
            
            return ApiResponse.ok("获取Banner图列表成功", resultList);
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }
    
    /**
     * 更新Banner状态
     * status: Enable - 启用, Disabled - 禁用
     */
    @PostMapping("/updateBannerStatus")
    public ApiResponse updateBannerStatus(@RequestBody Map<String, Object> req) {
        Map<String, Object> data = parseDataMap(req);
        if (data == null) {
            return ApiResponse.error(400, "参数不合法");
        }
        try {
            int result = miniBannerService.updateBannerStatus(data);
            return switch (result) {
                case 0 -> {
                    String status = data.get("status").toString();
                    String message = "Enable".equals(status) ? "Banner已启用" : "Banner已禁用";
                    yield ApiResponse.ok(message, null);
                }
                case 1 -> ApiResponse.error(400, "参数不合法");
                case 2 -> ApiResponse.error(400, "操作不合法");
                case 3 -> ApiResponse.error(400, "Banner不存在");
                default -> ApiResponse.error(500, "系统错误请稍后再试");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }
} 