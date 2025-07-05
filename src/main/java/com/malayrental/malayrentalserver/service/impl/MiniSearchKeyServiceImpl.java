package com.malayrental.malayrentalserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.malayrental.malayrentalserver.common.IdGeneratorUtil;
import com.malayrental.malayrentalserver.dao.MiniSearchKeyMapper;
import com.malayrental.malayrentalserver.dao.UserAccountMapper;
import com.malayrental.malayrentalserver.pojo.MiniSearchKey;
import com.malayrental.malayrentalserver.pojo.UserAccount;
import com.malayrental.malayrentalserver.service.MiniSearchKeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class MiniSearchKeyServiceImpl implements MiniSearchKeyService {
    private static final Logger logger = LoggerFactory.getLogger(MiniSearchKeyServiceImpl.class);
    private final MiniSearchKeyMapper miniSearchKeyMapper;
    private final UserAccountMapper userAccountMapper;

    public MiniSearchKeyServiceImpl(MiniSearchKeyMapper miniSearchKeyMapper, UserAccountMapper userAccountMapper) {
        this.miniSearchKeyMapper = miniSearchKeyMapper;
        this.userAccountMapper = userAccountMapper;
    }

    @Override
    public List<MiniSearchKey> getEnableSearchKeys() {
        QueryWrapper<MiniSearchKey> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "Enable").orderByAsc("type", "sort");
        return miniSearchKeyMapper.selectList(wrapper);
    }

    @Override
    public int addSearchKey(Map<String, Object> data) {
        try {
            String runUserId = (String) data.get("runUser");
            logger.info("addSearchKey runUserId: {}", runUserId);
            if (runUserId == null) return 2;
            UserAccount runUser = userAccountMapper.selectById(runUserId);
            logger.info("addSearchKey runUser: {}", runUser);
            if (runUser != null) {
                logger.info("addSearchKey userRole: {}", runUser.getUserRole());
            }
            if (runUser == null || !"Admin".equals(runUser.getUserRole())) {
                logger.warn("addSearchKey 权限校验失败, userId: {}, userRole: {}", runUserId, runUser != null ? runUser.getUserRole() : null);
                return 2; // 操作不合法
            }
            MiniSearchKey key = new MiniSearchKey();
            key.setId(IdGeneratorUtil.generateId(miniSearchKeyMapper, "id", "SKEY"));
            key.setType((String) data.get("type"));
            key.setKeyword((String) data.get("keyword"));
            key.setSort(data.get("sort") != null ? Integer.parseInt(data.get("sort").toString()) : 0);
            key.setStatus("Enable");
            return miniSearchKeyMapper.insert(key) > 0 ? 0 : 1;
        } catch (Exception e) {
            logger.error("addSearchKey异常", e);
            return 1;
        }
    }

    @Override
    public int deleteSearchKey(Map<String, Object> data) {
        try {
            String runUserId = (String) data.get("runUser");
            logger.info("deleteSearchKey runUserId: {}", runUserId);
            if (runUserId == null) return 2;
            UserAccount runUser = userAccountMapper.selectById(runUserId);
            logger.info("deleteSearchKey runUser: {}", runUser);
            if (runUser != null) {
                logger.info("deleteSearchKey userRole: {}", runUser.getUserRole());
            }
            if (runUser == null || !"Admin".equals(runUser.getUserRole())) {
                logger.warn("deleteSearchKey 权限校验失败, userId: {}, userRole: {}", runUserId, runUser != null ? runUser.getUserRole() : null);
                return 2; // 操作不合法
            }
            String id = (String) data.get("id");
            return miniSearchKeyMapper.deleteById(id) > 0 ? 0 : 1;
        } catch (Exception e) {
            logger.error("deleteSearchKey异常", e);
            return 1;
        }
    }
}