package com.malayrental.malayrentalserver.service.impl;

import com.malayrental.malayrentalserver.service.ImageService;
import com.malayrental.malayrentalserver.service.UserAccountService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.InputStream;

@Service
public class ImageServiceImpl implements ImageService {

    @Value("${malayrental.upload.image-path}")
    private String uploadPath;

    @Value("${dev.flag:false}")
    private boolean devFlag;

    @Value("${dev.remote-image-base-url:http://真实服务器地址/api/images}")
    private String remoteImageBaseUrl;

    private final UserAccountService userAccountService;
    
    // 允许的图片类型
    private final List<String> allowedTypes = Arrays.asList("avatar", "houseCover", "houseDetail", "chat", "banner");
    
    // 允许的图片扩展名
    private final List<String> allowedExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif");

    public ImageServiceImpl(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @Override
    public Resource downloadImage(String type, String filename) {
        // 验证类型是否合法
        if (isAllowedType(type)) {
            return null;
        }
        if (devFlag) {
            // 开发环境下转发到远程服务器
            try {
                String remoteUrl = remoteImageBaseUrl + "/" + type + "/" + filename;
                HttpURLConnection conn = (HttpURLConnection) new java.net.URL(remoteUrl).openConnection();
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(5000);
                conn.setRequestMethod("GET");
                int code = conn.getResponseCode();
                if (code == 200) {
                    InputStream in = conn.getInputStream();
                    return new InputStreamResource(in);
                } else {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        }
        try {
            // 构建文件路径
            Path filePath = Paths.get(uploadPath, type, filename);
            Resource resource = new UrlResource(filePath.toUri());
            
            // 检查文件是否存在且可读
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                return null;
            }
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @Override
    public Map<String, Object> uploadImage(String type, MultipartFile file, String runUser) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200); // 默认成功
        
        // 验证类型是否合法
        if (isAllowedType(type)) {
            result.put("code", 400); // 参数不合法
            result.put("message", "图片类型不合法");
            return result;
        }
        
        // 验证权限
        int permissionCheck = userAccountService.checkUserPermission(runUser);
        if (permissionCheck != 0) {
            result.put("code", 400); // 操作不合法
            result.put("message", "操作不合法");
            return result;
        }
        
        // 验证文件是否为空
        if (file.isEmpty()) {
            result.put("code", 400); // 参数不合法
            result.put("message", "上传的文件不能为空");
            return result;
        }
        
        // 获取原始文件名和扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            result.put("code", 400); // 参数不合法
            result.put("message", "文件名错误");
            return result;
        }
        
        String fileExtension = getFileExtension(originalFilename);
        
        // 验证扩展名是否合法
        if (!allowedExtensions.contains(fileExtension.toLowerCase())) {
            result.put("code", 400); // 参数不合法
            result.put("message", "不支持的文件类型，仅支持JPG, JPEG, PNG, GIF格式");
            return result;
        }
        
        if (devFlag) {
            // 开发环境下转发上传到远程服务器
            try {
                String remoteUrl = remoteImageBaseUrl + "/" + type + "/upload";
                java.net.URL url = new java.net.URL(remoteUrl);
                String boundary = "----WebKitFormBoundary" + UUID.randomUUID();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                java.io.OutputStream out = conn.getOutputStream();
                java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.OutputStreamWriter(out, java.nio.charset.StandardCharsets.UTF_8), true);
                // 发送runUser字段
                writer.append("--" + boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"runUser\"").append("\r\n\r\n");
                writer.append(runUser).append("\r\n");
                // 发送文件字段
                writer.append("--" + boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + originalFilename + "\"").append("\r\n");
                writer.append("Content-Type: " + determineContentType(originalFilename)).append("\r\n\r\n");
                writer.flush();
                // 写入文件内容
                try (InputStream inputStream = file.getInputStream()) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                    out.flush();
                }
                writer.append("\r\n");
                // 结束分隔符
                writer.append("--" + boundary + "--").append("\r\n");
                writer.close();
                // 读取远程响应
                int code = conn.getResponseCode();
                InputStream respStream = (code == 200) ? conn.getInputStream() : conn.getErrorStream();
                StringBuilder resp = new StringBuilder();
                try (Scanner scanner = new Scanner(respStream, java.nio.charset.StandardCharsets.UTF_8)) {
                    while (scanner.hasNextLine()) {
                        resp.append(scanner.nextLine());
                    }
                }
                String respStr = resp.toString();
                // 直接解析远程返回的JSON并合并到result
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> remoteMap = mapper.readValue(respStr, Map.class);
                result.clear();
                result.putAll(remoteMap);
                return result;
            } catch (Exception e) {
                result.put("code", 500);
                result.put("message", "转发上传失败: " + e.getMessage());
                return result;
            }
        }
        try {
            // 生成新的文件名
            String newFilename = UUID.randomUUID() + fileExtension;
            
            // 确保目标目录存在
            File targetDir = new File(uploadPath + File.separator + type);
            if (!targetDir.exists()) {
                if (!targetDir.mkdirs()) {
                    result.put("code", 500); // 服务器错误
                    result.put("message", "创建目录失败");
                    return result;
                }
            }
            
            // 保存文件
            Path targetPath = Paths.get(uploadPath, type, newFilename);
            Files.copy(file.getInputStream(), targetPath);
            
            // 返回成功结果和文件信息
            result.put("filename", newFilename);
            result.put("url", "/api/images/" + type + "/" + newFilename);
            
            return result;
        } catch (IOException e) {
            result.put("code", 500); // 服务器错误
            result.put("message", "文件上传失败: " + e.getMessage());
            return result;
        }
    }

    @Override
    public boolean isAllowedType(String type) {
        return !allowedTypes.contains(type);
    }

    @Override
    public String determineContentType(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return switch (extension) {
            case ".png" -> "image/png";
            case ".gif" -> "image/gif";
            case ".jpg", ".jpeg" -> "image/jpeg";
            default -> "application/octet-stream";
        };
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0) {
            return filename.substring(dotIndex);
        }
        return "";
    }
}