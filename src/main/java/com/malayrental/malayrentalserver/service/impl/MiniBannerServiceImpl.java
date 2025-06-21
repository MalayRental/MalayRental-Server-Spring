package com.malayrental.malayrentalserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.malayrental.malayrentalserver.common.IdGeneratorUtil;
import com.malayrental.malayrentalserver.dao.MiniBannerMapper;
import com.malayrental.malayrentalserver.dao.UserAccountMapper;
import com.malayrental.malayrentalserver.pojo.MiniBanner;
import com.malayrental.malayrentalserver.pojo.UserAccount;
import com.malayrental.malayrentalserver.service.MiniBannerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class MiniBannerServiceImpl implements MiniBannerService {
    private final MiniBannerMapper miniBannerMapper;
    private final UserAccountMapper userAccountMapper;

    @Value("${malayrental.upload.image-path}")
    private String uploadPath;

    public MiniBannerServiceImpl(MiniBannerMapper miniBannerMapper, UserAccountMapper userAccountMapper) {
        this.miniBannerMapper = miniBannerMapper;
        this.userAccountMapper = userAccountMapper;
    }

    @Override
    public int createBanner(Map<String, Object> data) {
        if (data == null || data.get("runUser") == null || data.get("image") == null || data.get("link") == null) {
            return 1; // 参数不合法
        }
        
        String runUserId = data.get("runUser").toString();
        String image = data.get("image").toString();
        String link = data.get("link").toString();
        
        try {
            // 检查操作用户是否为Admin
            UserAccount runUser = userAccountMapper.selectById(runUserId);
            if (runUser == null || !"Admin".equals(runUser.getUserRole())) {
                return 2; // 操作不合法
            }
            
            // 检查图片是否存在于banner目录
            Path imagePath = Paths.get(uploadPath, "banner", image);
            if (!Files.exists(imagePath)) {
                return 3; // Banner图片不存在
            }
            
            // 创建MiniBanner对象
            MiniBanner banner = new MiniBanner();
            banner.setBannerId(IdGeneratorUtil.generateId(miniBannerMapper, "banner_id", "BANNER"));
            banner.setImage(image);
            banner.setLink(link);
            banner.setStatus("Disabled"); // 默认为Disabled状态
            banner.setCreateTime(LocalDateTime.now());
            
            // 插入数据
            int result = miniBannerMapper.insert(banner);
            return result > 0 ? 0 : 5; // 0-成功，5-系统错误
        } catch (Exception e) {
            return 5; // 系统错误
        }
    }

    @Override
    public List<MiniBanner> getBannerList() {
        try {
            QueryWrapper<MiniBanner> wrapper = new QueryWrapper<>();
            return miniBannerMapper.selectList(wrapper);
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public int updateBannerStatus(Map<String, Object> data) {
        if (data == null || data.get("runUser") == null || data.get("bannerId") == null || data.get("status") == null) {
            return 1; // 参数不合法
        }
        
        String runUserId = data.get("runUser").toString();
        String bannerId = data.get("bannerId").toString();
        String status = data.get("status").toString();
        
        // 检查status是否合法
        if (!status.equals("Enable") && !status.equals("Disabled")) {
            return 1; // 参数不合法
        }
        
        try {
            // 检查操作用户是否为Admin
            UserAccount runUser = userAccountMapper.selectById(runUserId);
            if (runUser == null || !"Admin".equals(runUser.getUserRole())) {
                return 2; // 操作不合法
            }
            
            // 检查Banner是否存在
            MiniBanner banner = miniBannerMapper.selectById(bannerId);
            if (banner == null) {
                return 3; // Banner不存在
            }
            
            // 更新状态
            banner.setStatus(status);
            int result = miniBannerMapper.updateById(banner);
            return result > 0 ? 0 : 5; // 0-成功，5-系统错误
        } catch (Exception e) {
            return 5; // 系统错误
        }
    }

    @Override
    public int editBanner(Map<String, Object> data) {
        if (data == null || data.get("runUser") == null || data.get("bannerId") == null || data.get("link") == null) {
            return 1; // 参数不合法
        }
        String runUserId = data.get("runUser").toString();
        String bannerId = data.get("bannerId").toString();
        String link = data.get("link").toString();
        try {
            // 检查操作用户是否为Admin
            UserAccount runUser = userAccountMapper.selectById(runUserId);
            if (runUser == null || !"Admin".equals(runUser.getUserRole())) {
                return 2; // 操作不合法
            }
            // 检查Banner是否存在
            MiniBanner banner = miniBannerMapper.selectById(bannerId);
            if (banner == null) {
                return 3; // Banner不存在
            }
            // 更新link
            banner.setLink(link);
            int result = miniBannerMapper.updateById(banner);
            return result > 0 ? 0 : 5; // 0-成功，5-系统错误
        } catch (Exception e) {
            return 5; // 系统错误
        }
    }

    @Override
    public int deleteBanner(Map<String, Object> data) {
        if (data == null || data.get("runUser") == null || data.get("bannerId") == null) {
            return 1; // 参数不合法
        }
        String runUserId = data.get("runUser").toString();
        String bannerId = data.get("bannerId").toString();
        try {
            // 检查操作用户是否为Admin
            UserAccount runUser = userAccountMapper.selectById(runUserId);
            if (runUser == null || !"Admin".equals(runUser.getUserRole())) {
                return 2; // 操作不合法
            }
            // 检查Banner是否存在
            MiniBanner banner = miniBannerMapper.selectById(bannerId);
            if (banner == null) {
                return 3; // Banner不存在
            }
            // 逻辑删除，status设为Deleted
            banner.setStatus("Deleted");
            int result = miniBannerMapper.updateById(banner);
            return result > 0 ? 0 : 5; // 0-成功，5-系统错误
        } catch (Exception e) {
            return 5; // 系统错误
        }
    }
}