package com.malayrental.malayrentalserver.controller;

import com.malayrental.malayrentalserver.common.ApiResponse;
import com.malayrental.malayrentalserver.service.ChatService;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseDataMap(Map<String, Object> req) {
        Object dataObj = req.get("data");
        if (!(dataObj instanceof Map)) {
            return null;
        }
        return (Map<String, Object>) dataObj;
    }

    private static class DataAndResultList {
        Map<String, Object> data;
        List<Map<String, Object>> resultList;
        DataAndResultList(Map<String, Object> data, List<Map<String, Object>> resultList) {
            this.data = data;
            this.resultList = resultList;
        }
    }

    private DataAndResultList getDataAndResultList(Map<String, Object> req) throws IllegalArgumentException {
        Map<String, Object> data = parseDataMap(req);
        if (data == null) {
            throw new IllegalArgumentException("参数不合法");
        }
        return new DataAndResultList(data, new ArrayList<>());
    }

    @PostMapping("/createChat")
    public ApiResponse createChat(@RequestBody Map<String, Object> req) {
        try {
            Map<String, Object> data = parseDataMap(req);
            if (data == null) {
                return ApiResponse.error(400, "参数不合法");
            }
            Map<String, Object> result = new HashMap<>();
            int code = chatService.createChat(data, result);
            return switch (code) {
                case 0 -> ApiResponse.ok("创建会话成功", result);
                case 1 -> ApiResponse.error(400, "参数不合法");
                case 2 -> ApiResponse.error(400, "用户不存在");
                case 3 -> ApiResponse.ok("会话已经存在", result);
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
            DataAndResultList tuple;
            try {
                tuple = getDataAndResultList(req);
            } catch (IllegalArgumentException e) {
                return ApiResponse.error(400, e.getMessage());
            }
            int code = chatService.getChatList(tuple.data, tuple.resultList);
            return switch (code) {
                case 0 -> ApiResponse.ok("获取会话成功", tuple.resultList);
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
            DataAndResultList tuple;
            try {
                tuple = getDataAndResultList(req);
            } catch (IllegalArgumentException e) {
                return ApiResponse.error(400, e.getMessage());
            }
            int code = chatService.getAllChatList(tuple.data, tuple.resultList);
            return switch (code) {
                case 0 -> ApiResponse.ok("获取会话成功", tuple.resultList);
                case 1 -> ApiResponse.error(400, "参数不合法");
                case 2 -> ApiResponse.error(400, "用户不存在");
                case 3 -> ApiResponse.error(400, "操作不合法");
                default -> ApiResponse.error(500, "系统错误请稍后再试");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }

    @PostMapping("/getAllMessages")
    public ApiResponse getAllMessages(@RequestBody Map<String, Object> req) {
        try {
            DataAndResultList tuple;
            try {
                tuple = getDataAndResultList(req);
            } catch (IllegalArgumentException e) {
                return ApiResponse.error(400, e.getMessage());
            }
            int code = chatService.getAllMessages(tuple.data, tuple.resultList);
            return switch (code) {
                case 0 -> ApiResponse.ok("获取会话消息历史成功", tuple.resultList);
                case 1 -> ApiResponse.error(400, "参数不合法");
                case 2 -> ApiResponse.error(400, "会话不存在");
                case 3 -> ApiResponse.error(400, "操作不合法");
                default -> ApiResponse.error(500, "系统错误请稍后再试");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }

    @PostMapping("/readChatMessages")
    public ApiResponse readChatMessages(@RequestBody Map<String, Object> req) {
        try {
            Map<String, Object> data = parseDataMap(req);
            if (data == null) {
                return ApiResponse.error(400, "参数不合法");
            }
            int code = chatService.readChatMessages(data);
            return switch (code) {
                case 0 -> ApiResponse.ok("已读会话消息成功", null);
                case 1 -> ApiResponse.error(400, "参数不合法");
                case 2 -> ApiResponse.error(400, "会话不存在");
                case 3 -> ApiResponse.error(400, "操作不合法");
                default -> ApiResponse.error(500, "系统错误请稍后再试");
            };
        } catch (Exception e) {
            return ApiResponse.error(500, "系统错误请稍后再试");
        }
    }
} 