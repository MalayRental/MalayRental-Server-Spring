package com.malayrental.malayrentalserver.controller;

import com.malayrental.malayrentalserver.common.ApiResponse;
import com.malayrental.malayrentalserver.service.ApprovalTaskService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/approval")
public class ApprovalTaskController {
    private final ApprovalTaskService approvalTaskService;

    public ApprovalTaskController(ApprovalTaskService approvalTaskService) {
        this.approvalTaskService = approvalTaskService;
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

    @PostMapping("/createApproval")
    public ApiResponse createApproval(@RequestBody Map<String, Object> req) {
        Map<String, Object> data = parseDataMap(req);
        if (data == null) {
            return ApiResponse.error(400, "参数不合法");
        }
        try {
            int result = approvalTaskService.createApproval(data);
            return switch (result) {
                case 0 -> ApiResponse.ok("审批任务创建成功", null);
                case 1 -> ApiResponse.error(400, "参数不合法");
                case 2 -> ApiResponse.error(400, "操作不合法");
                case 3 -> ApiResponse.error(400, "你有待审批的任务，请先处理未完成审批的任务");
                default -> ApiResponse.error(500, "系统错误请稍后再试");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }

    @PostMapping("/finishApproval")
    public ApiResponse finishApproval(@RequestBody Map<String, Object> req) {
        Map<String, Object> data = parseDataMap(req);
        if (data == null) {
            return ApiResponse.error(400, "参数不合法");
        }
        try {
            int result = approvalTaskService.finishApproval(data);
            return switch (result) {
                case 0 -> ApiResponse.ok("审批已通过", null);
                case 10 -> ApiResponse.ok("审批任务已拒绝", null);
                case 1 -> ApiResponse.error(400, "参数不合法");
                case 2 -> ApiResponse.error(400, "操作不合法");
                case 3 -> ApiResponse.error(400, "审批任务状态错误");
                case 4 -> ApiResponse.error(400, "未知命令，执行失败");
                case 5 -> ApiResponse.error(400, "命令执行异常");
                case 6 -> ApiResponse.error(400, "命令执行失败");
                default -> ApiResponse.error(500, "系统错误请稍后再试");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }
} 