package com.malayrental.malayrentalserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.malayrental.malayrentalserver.common.IdGeneratorUtil;
import com.malayrental.malayrentalserver.dao.ApprovalTaskMapper;
import com.malayrental.malayrentalserver.dao.UserAccountMapper;
import com.malayrental.malayrentalserver.pojo.ApprovalTask;
import com.malayrental.malayrentalserver.pojo.UserAccount;
import com.malayrental.malayrentalserver.service.ApprovalTaskService;
import com.malayrental.malayrentalserver.service.HouseListService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ApprovalTaskServiceImpl implements ApprovalTaskService {
    private final ApprovalTaskMapper approvalTaskMapper;
    private final UserAccountMapper userAccountMapper;
    private final HouseListService houseListService;

    public ApprovalTaskServiceImpl(ApprovalTaskMapper approvalTaskMapper, UserAccountMapper userAccountMapper, HouseListService houseListService) {
        this.approvalTaskMapper = approvalTaskMapper;
        this.userAccountMapper = userAccountMapper;
        this.houseListService = houseListService;
    }

    @Override
    public int createApproval(Map<String, Object> data) {
        if (data == null || data.get("runUser") == null || data.get("title") == null
                || data.get("desc") == null || data.get("command") == null) {
            return 1; // 参数不合法
        }
        String runUserId = data.get("runUser").toString();
        try {
            UserAccount runUser = userAccountMapper.selectById(runUserId);
            if (runUser == null || runUser.getUserRole() == null ||
                !("Admin".equals(runUser.getUserRole()) || "Staff".equals(runUser.getUserRole()))) {
                return 2; // 操作不合法
            }
            // 检查是否有未完成审批
            QueryWrapper<ApprovalTask> wrapper = new QueryWrapper<>();
            wrapper.eq("create_user", runUserId).eq("status", "Pending");
            if (approvalTaskMapper.selectCount(wrapper) > 0) {
                return 3; // 有待审批任务
            }
            ApprovalTask task = new ApprovalTask();
            task.setApprovalId(IdGeneratorUtil.generateId(approvalTaskMapper, "approval_id", "APPROVAL"));
            task.setTitle(data.get("title").toString());
            task.setDesc(data.get("desc").toString());
            task.setCommand(data.get("command").toString());
            task.setCreateUser(runUserId);
            task.setCreateTime(LocalDateTime.now());
            task.setStatus("Pending");
            int rows = approvalTaskMapper.insert(task);
            return rows > 0 ? 0 : 5;
        } catch (Exception e) {
            return 5; // 系统错误
        }
    }

    @Override
    public int finishApproval(Map<String, Object> data) {
        if (data == null || data.get("runUser") == null || data.get("approvalId") == null || data.get("status") == null) {
            return 1; // 参数不合法
        }
        String runUserId = data.get("runUser").toString();
        String approvalId = data.get("approvalId").toString();
        String status = data.get("status").toString();
        String rejectReason = data.get("rejectReason") != null ? data.get("rejectReason").toString() : null;
        try {
            UserAccount runUser = userAccountMapper.selectById(runUserId);
            if (runUser == null || runUser.getUserRole() == null || !"Admin".equals(runUser.getUserRole())) {
                return 2; // 操作不合法
            }
            ApprovalTask task = approvalTaskMapper.selectById(approvalId);
            if (task == null || !"Pending".equals(task.getStatus())) {
                return 3; // 审批任务状态错误
            }
            // 如果是拒绝，直接更新，不执行command
            if ("Rejected".equals(status)) {
                if (rejectReason == null || rejectReason.isEmpty()) {
                    return 1; // 参数不合法
                }
                task.setStatus(status);
                task.setFinishUser(runUserId);
                task.setFinishTime(LocalDateTime.now());
                task.setRejectReason(rejectReason);
                int rows = approvalTaskMapper.updateById(task);
                return rows > 0 ? 10 : 5; // 10表示已拒绝
            }
            // 解析命令并执行
            String command = task.getCommand();
            boolean commandResult = false;
            String commandError = null;
            if (command != null && command.startsWith("[house_list][") && command.contains("][SetStatus][")) {
                try {
                    String[] parts = command.split("]");
                    String houseId = parts[1].substring(1);
                    String statusValue = parts[3].substring(1);
                    commandResult = ((com.malayrental.malayrentalserver.service.impl.HouseListServiceImpl)houseListService).setHouseStatus(houseId, statusValue);
                } catch (Exception e) {
                    commandError = e.getMessage();
                }
            } else if (command != null && command.startsWith("[house_list][") && command.contains("][Delete]")) {
                try {
                    String[] parts = command.split("]");
                    String houseId = parts[1].substring(1);
                    commandResult = ((com.malayrental.malayrentalserver.service.impl.HouseListServiceImpl)houseListService).deleteHouse(houseId);
                } catch (Exception e) {
                    commandError = e.getMessage();
                }
            } else {
                return 4; // 未知命令，执行失败
            }
            if (commandError != null) {
                return 5;
            }
            if (!commandResult) {
                return 6;
            }
            task.setStatus(status);
            task.setFinishUser(runUserId);
            task.setFinishTime(LocalDateTime.now());
            int rows = approvalTaskMapper.updateById(task);
            return rows > 0 ? 0 : 5;
        } catch (Exception e) {
            return 5; // 系统错误
        }
    }
} 