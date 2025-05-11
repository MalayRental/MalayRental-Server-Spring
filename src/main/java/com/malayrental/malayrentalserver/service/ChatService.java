package com.malayrental.malayrentalserver.service;

import java.util.Map;

public interface ChatService {
    int createChat(Map<String, Object> data, Map<String, Object> result);
    int getChatList(Map<String, Object> data, java.util.List<java.util.Map<String, Object>> resultList);
    int getAllChatList(Map<String, Object> data, java.util.List<java.util.Map<String, Object>> resultList);
} 