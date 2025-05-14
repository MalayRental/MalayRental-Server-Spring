package com.malayrental.malayrentalserver.controller;

import com.malayrental.malayrentalserver.common.ApiResponse;
import com.malayrental.malayrentalserver.service.ImageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * 下载图片
     * @param type 图片类型：avatar, houseCover, houseDetail, chat, banner
     * @param filename 图片文件名
     * @return 图片文件
     */
    @GetMapping("/{type}/{filename:.+}")
    public ResponseEntity<Resource> downloadImage(
            @PathVariable String type,
            @PathVariable String filename) {
        
        // 检查类型是否合法
        if (imageService.isAllowedType(type)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        
        // 调用服务层下载图片
        Resource resource = imageService.downloadImage(type, filename);
        
        // 检查资源是否存在
        if (resource == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        // 设置内容类型
        String contentType = imageService.determineContentType(filename);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
    }

    /**
     * 上传图片
     * @param type 图片类型：avatar, houseCover, houseDetail, chat, banner
     * @param file 上传的文件
     * @param runUser 操作用户ID
     * @return 上传结果
     */
    @PostMapping("/upload/{type}")
    public ApiResponse uploadImage(
            @PathVariable String type,
            @RequestParam("file") MultipartFile file,
            @RequestParam("runUser") String runUser) {
        
        try {
            // 调用服务层上传图片
            Map<String, Object> result = imageService.uploadImage(type, file, runUser);
            
            // 获取结果代码
            int code = (int) result.get("code");
            
            // 根据代码返回不同的响应
            if (code == 200) {
                // 上传成功
                return ApiResponse.ok("上传成功", Map.of(
                        "filename", result.get("filename"),
                        "url", result.get("url")
                ));
            } else if (code == 400) {
                // 参数不合法或操作不合法
                return ApiResponse.error(400, (String) result.get("message"));
            } else {
                // 服务器错误
                return ApiResponse.error(500, (String) result.get("message"));
            }
            
        } catch (Exception e) {
            // 捕获所有异常，确保总是返回规范的错误
            return ApiResponse.error(500, "服务器错误: " + e.getMessage());
        }
    }
} 