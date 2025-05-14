package com.malayrental.malayrentalserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.malayrental.malayrentalserver.common.IdGeneratorUtil;
import com.malayrental.malayrentalserver.dao.HouseAreaMapper;
import com.malayrental.malayrentalserver.dao.UserAccountMapper;
import com.malayrental.malayrentalserver.pojo.HouseArea;
import com.malayrental.malayrentalserver.pojo.UserAccount;
import com.malayrental.malayrentalserver.service.HouseAreaService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class HouseAreaServiceImpl implements HouseAreaService {
    private final HouseAreaMapper houseAreaMapper;
    private final UserAccountMapper userAccountMapper;

    public HouseAreaServiceImpl(HouseAreaMapper houseAreaMapper, UserAccountMapper userAccountMapper) {
        this.houseAreaMapper = houseAreaMapper;
        this.userAccountMapper = userAccountMapper;
    }

    @Override
    public int createArea(Map<String, Object> data) {
        if (data == null || data.get("runUser") == null || data.get("areaName") == null
                || data.get("address") == null || data.get("latLng") == null) {
            return 1; // 参数不合法
        }
        String runUserId = data.get("runUser").toString();
        String areaName = data.get("areaName").toString();
        String address = data.get("address").toString();
        String latLng = data.get("latLng").toString();
        String desc = data.get("desc") == null ? null : data.get("desc").toString();
        try {
            UserAccount runUser = userAccountMapper.selectById(runUserId);
            if (runUser == null || !"Admin".equals(runUser.getUserRole())) {
                return 2; // 操作不合法
            }
            
            // 使用selectCount检查区域名是否存在
            QueryWrapper<HouseArea> wrapper = new QueryWrapper<>();
            wrapper.eq("area_name", areaName);
            if (houseAreaMapper.selectCount(wrapper) > 0) {
                return 3; // 区域已存在
            }
            
            HouseArea area = new HouseArea();
            area.setAreaId(IdGeneratorUtil.generateId(houseAreaMapper, "area_id", "A"));
            area.setAreaName(areaName);
            area.setAddress(address);
            area.setLatLng(latLng);
            area.setDesc(desc);
            area.setCreateUser(runUserId);
            area.setCreateTime(LocalDateTime.now());
            area.setUpdateTime(LocalDateTime.now());
            
            int rows = houseAreaMapper.insert(area);
            return rows > 0 ? 0 : 4;
        } catch (Exception e) {
            return 4; // 系统错误
        }
    }

    /**
     * 校验操作人和目标区域
     * @return 0-校验通过，2-操作不合法，3-区域不存在
     */
    private int checkAdminAndArea(String runUserId, String areaId) {
        UserAccount runUser = userAccountMapper.selectById(runUserId);
        if (runUser == null || !"Admin".equals(runUser.getUserRole())) {
            return 2; // 操作不合法
        }
        HouseArea area = houseAreaMapper.selectById(areaId);
        if (area == null) {
            return 3; // 区域不存在
        }
        return 0;
    }

    @Override
    public int deleteArea(Map<String, Object> data) {
        if (data == null || data.get("runUser") == null || data.get("areaId") == null) {
            return 1; // 参数不合法
        }
        String runUserId = data.get("runUser").toString();
        String areaId = data.get("areaId").toString();
        try {
            int check = checkAdminAndArea(runUserId, areaId);
            if (check != 0) return check;
            int rows = houseAreaMapper.deleteById(areaId);
            return rows > 0 ? 0 : 4;
        } catch (Exception e) {
            return 4; // 系统错误
        }
    }

    @Override
    public int updateArea(Map<String, Object> data) {
        if (data == null || data.get("runUser") == null || data.get("areaId") == null) {
            return 1; // 参数不合法
        }
        String runUserId = data.get("runUser").toString();
        String areaId = data.get("areaId").toString();
        try {
            int check = checkAdminAndArea(runUserId, areaId);
            if (check != 0) return check;
            HouseArea area = houseAreaMapper.selectById(areaId);
            if (data.containsKey("areaName")) {
                area.setAreaName(data.get("areaName").toString());
            }
            if (data.containsKey("address")) {
                area.setAddress(data.get("address").toString());
            }
            if (data.containsKey("latLng")) {
                area.setLatLng(data.get("latLng").toString());
            }
            if (data.containsKey("desc")) {
                area.setDesc(data.get("desc").toString());
            }
            area.setUpdateTime(java.time.LocalDateTime.now());
            int rows = houseAreaMapper.updateById(area);
            return rows > 0 ? 0 : 4;
        } catch (Exception e) {
            return 4; // 系统错误
        }
    }

    @Override
    public int getAreaList(Map<String, Object> data, java.util.List<Map<String, Object>> resultList) {
        try {
            java.util.List<HouseArea> list = houseAreaMapper.selectList(null);
            for (HouseArea area : list) {
                java.util.Map<String, Object> map = new java.util.HashMap<>();
                map.put("areaId", area.getAreaId());
                map.put("areaName", area.getAreaName());
                resultList.add(map);
            }
            return 0;
        } catch (Exception e) {
            return 4; // 系统错误
        }
    }

    @Override
    public int getAreaDetail(Map<String, Object> data, Map<String, Object> result) {
        if (data == null || data.get("runUser") == null || data.get("areaId") == null) {
            return 1; // 参数不合法
        }
        String runUserId = data.get("runUser").toString();
        String areaId = data.get("areaId").toString();
        try {
            int check = checkAdminAndArea(runUserId, areaId);
            if (check != 0) return check;
            HouseArea area = houseAreaMapper.selectById(areaId);
            result.put("runUser", runUserId);
            result.put("areaId", area.getAreaId());
            result.put("areaName", area.getAreaName());
            result.put("address", area.getAddress());
            result.put("latLng", area.getLatLng());
            result.put("desc", area.getDesc());
            result.put("createUser", area.getCreateUser());
            result.put("updateTime", area.getUpdateTime());
            result.put("createTime", area.getCreateTime());
            return 0;
        } catch (Exception e) {
            return 4; // 系统错误
        }
    }
} 