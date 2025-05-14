package com.malayrental.malayrentalserver.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface ImageService {
    
    /**
     * 下载图片
     * @param type 图片类型
     * @param filename 文件名
     * @return 资源对象，如果不存在或类型不合法则返回null
     */
    Resource downloadImage(String type, String filename);
    
    /**
     * 上传图片
     * @param type 图片类型
     * @param file 文件
     * @param runUser 执行用户ID
     * @return 上传结果，包含以下字段：
     *         - code: 200-成功，400-参数不合法或操作不合法，500-服务器错误
     *         - message: 错误信息（当code不为200时）
     *         - filename: 保存的文件名（当code为200时）
     *         - url: 文件访问URL（当code为200时）
     */
    Map<String, Object> uploadImage(String type, MultipartFile file, String runUser) throws IOException;
    
    /**
     * 检查图片类型是否合法
     * @param type 图片类型
     * @return 类型是否合法
     */
    boolean isAllowedType(String type);
    
    /**
     * 获取文件的内容类型
     * @param filename 文件名
     * @return 内容类型
     */
    String determineContentType(String filename);
} 