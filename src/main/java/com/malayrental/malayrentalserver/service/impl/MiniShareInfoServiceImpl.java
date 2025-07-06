package com.malayrental.malayrentalserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.malayrental.malayrentalserver.dao.MiniShareInfoMapper;
import com.malayrental.malayrentalserver.dao.UserAccountMapper;
import com.malayrental.malayrentalserver.pojo.MiniShareInfo;
import com.malayrental.malayrentalserver.pojo.UserAccount;
import com.malayrental.malayrentalserver.service.MiniShareInfoService;
import com.malayrental.malayrentalserver.common.IdGeneratorUtil;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class MiniShareInfoServiceImpl implements MiniShareInfoService {
    private final MiniShareInfoMapper miniShareInfoMapper;
    private final UserAccountMapper userAccountMapper;

    public MiniShareInfoServiceImpl(MiniShareInfoMapper miniShareInfoMapper, UserAccountMapper userAccountMapper) {
        this.miniShareInfoMapper = miniShareInfoMapper;
        this.userAccountMapper = userAccountMapper;
    }

    @Override
    public MiniShareInfo getEnableShareInfoByPage(String page) {
        QueryWrapper<MiniShareInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("page", page).eq("status", "Enable").last("limit 1");
        return miniShareInfoMapper.selectOne(wrapper);
    }

    @Override
    public int addShareInfo(Map<String, Object> data) {
        try {
            String runUserId = (String) data.get("runUser");
            if (runUserId == null) return 2;
            UserAccount runUser = userAccountMapper.selectById(runUserId);
            if (runUser == null || !"Admin".equals(runUser.getUserRole())) {
                return 2;
            }
            String page = (String) data.get("page");
            String title = (String) data.get("title");
            String imageUrl = (String) data.get("imageUrl");
            if (page == null || title == null) return 1;
            QueryWrapper<MiniShareInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("page", page);
            if (miniShareInfoMapper.selectCount(wrapper) > 0) return 2;
            MiniShareInfo info = new MiniShareInfo();
            info.setId(IdGeneratorUtil.generateId(miniShareInfoMapper, "id", "SHARE"));
            info.setPage(page);
            info.setTitle(title);
            info.setImageUrl(imageUrl);
            info.setStatus("Enable");
            info.setCreateTime(LocalDateTime.now());
            info.setUpdateTime(LocalDateTime.now());
            return miniShareInfoMapper.insert(info) > 0 ? 0 : 1;
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public int updateShareInfo(Map<String, Object> data) {
        try {
            String runUserId = (String) data.get("runUser");
            if (runUserId == null) return 2;
            UserAccount runUser = userAccountMapper.selectById(runUserId);
            if (runUser == null || !"Admin".equals(runUser.getUserRole())) {
                return 2;
            }
            String page = (String) data.get("page");
            String title = (String) data.get("title");
            String imageUrl = (String) data.get("imageUrl");
            if (page == null || title == null) return 1;
            QueryWrapper<MiniShareInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("page", page);
            MiniShareInfo info = miniShareInfoMapper.selectOne(wrapper);
            if (info == null) return 3;
            info.setTitle(title);
            info.setImageUrl(imageUrl);
            info.setUpdateTime(LocalDateTime.now());
            return miniShareInfoMapper.updateById(info) > 0 ? 0 : 1;
        } catch (Exception e) {
            return 1;
        }
    }
} 