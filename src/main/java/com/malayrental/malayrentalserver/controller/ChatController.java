package com.malayrental.malayrentalserver.controller;

import com.malayrental.malayrentalserver.common.ApiResponse;
import com.malayrental.malayrentalserver.service.ChatService;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/createChat")
    public ApiResponse createChat(@RequestBody Map<String, Object> req) {
        try {
            Object dataObj = req.get("data");
            if (!(dataObj instanceof Map)) {
                return ApiResponse.error(400, "参数不合法");
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) dataObj;
            Map<String, Object> result = new HashMap<>();
            int code = chatService.createChat(data, result);
            return switch (code) {
                case 0 -> ApiResponse.ok("创建会话成功", result);
                case 1 -> ApiResponse.error(400, "参数不合法");
                case 2 -> ApiResponse.error(400, "用户不存在");
                case 3 -> ApiResponse.error(400, "对话已存在");
                case 5 -> ApiResponse.error(400, "您无法向自己发起会话");
                default -> ApiResponse.error(500, "系统错误请稍后再试");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }

    @PostMapping("/getChatList")
    public ApiResponse getChatList(@RequestBody Map<String, Object> req) {
        try {
            Object dataObj = req.get("data");
            if (!(dataObj instanceof Map)) {
                return ApiResponse.error(400, "参数不合法");
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) dataObj;
            java.util.List<java.util.Map<String, Object>> resultList = new java.util.ArrayList<>();
            int code = chatService.getChatList(data, resultList);
            return switch (code) {
                case 0 -> ApiResponse.ok("获取会话成功", resultList);
                case 1 -> ApiResponse.error(400, "参数不合法");
                case 2 -> ApiResponse.error(400, "用户不存在");
                default -> ApiResponse.error(500, "系统错误请稍后再试");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }

    @PostMapping("/getAllChatList")
    public ApiResponse getAllChatList(@RequestBody Map<String, Object> req) {
        try {
            Object dataObj = req.get("data");
            if (!(dataObj instanceof Map)) {
                return ApiResponse.error(400, "参数不合法");
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) dataObj;
            java.util.List<java.util.Map<String, Object>> resultList = new java.util.ArrayList<>();
            int code = chatService.getAllChatList(data, resultList);
            return switch (code) {
                case 0 -> ApiResponse.ok("获取会话成功", resultList);
                case 1 -> ApiResponse.error(400, "参数不合法");
                case 2 -> ApiResponse.error(400, "用户不存在");
                case 3 -> ApiResponse.error(400, "操作不合法");
                default -> ApiResponse.error(500, "系统错误请稍后再试");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }
} 