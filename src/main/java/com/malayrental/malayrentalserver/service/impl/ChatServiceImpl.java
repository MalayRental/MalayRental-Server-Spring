package com.malayrental.malayrentalserver.service.impl;

import com.malayrental.malayrentalserver.dao.ChatListMapper;
import com.malayrental.malayrentalserver.dao.UserAccountMapper;
import com.malayrental.malayrentalserver.dao.MessageListMapper;
import com.malayrental.malayrentalserver.pojo.MessageList;
import com.malayrental.malayrentalserver.pojo.ChatList;
import com.malayrental.malayrentalserver.pojo.UserAccount;
import com.malayrental.malayrentalserver.service.ChatService;
import com.malayrental.malayrentalserver.common.IdGeneratorUtil;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ChatServiceImpl implements ChatService {
    private final ChatListMapper chatListMapper;
    private final UserAccountMapper userAccountMapper;
    private final MessageListMapper messageListMapper;

    public ChatServiceImpl(ChatListMapper chatListMapper, UserAccountMapper userAccountMapper, MessageListMapper messageListMapper) {
        this.chatListMapper = chatListMapper;
        this.userAccountMapper = userAccountMapper;
        this.messageListMapper = messageListMapper;
    }

    @Override
    public int createChat(Map<String, Object> data, Map<String, Object> result) {
        if (data == null || data.get("runUser") == null || data.get("staffId") == null) {
            return 1; // 参数不合法
        }
        String userId = data.get("runUser").toString();
        String staffId = data.get("staffId").toString();
        if (userId.equals(staffId)) {
            return 5; // 不能向自己发起会话
        }
        try {
            UserAccount user = userAccountMapper.selectById(userId);
            UserAccount staff = userAccountMapper.selectById(staffId);
            if (user == null || staff == null) {
                return 2; // 用户不存在
            }
            QueryWrapper<ChatList> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", userId).eq("staff_id", staffId);
            ChatList existChat = chatListMapper.selectOne(wrapper);
            if (existChat != null) {
                result.put("chatId", existChat.getChatId());
                result.put("staffAvatar", staff.getAvatar());
                result.put("staffStatus", staff.getOnlineStatus());
                result.put("staffName", staff.getUserName());
                result.put("staffId", staffId);
                return 3; // 对话已存在
            }
            ChatList chat = new ChatList();
            chat.setChatId(IdGeneratorUtil.generateId(chatListMapper, "chat_id", "C"));
            chat.setUserId(userId);
            chat.setStaffId(staffId);
            chat.setCreateTime(LocalDateTime.now());
            int rows = chatListMapper.insert(chat);
            if (rows > 0) {
                result.put("chatId", chat.getChatId());
                result.put("staffAvatar", staff.getAvatar());
                result.put("staffStatus", staff.getOnlineStatus());
                result.put("staffName", staff.getUserName());
                result.put("staffId", staffId);
                return 0;
            } else {
                return 4; // 系统错误
            }
        } catch (Exception e) {
            return 4; // 系统错误
        }
    }

    private void buildChatList(java.util.List<ChatList> chatList, java.util.List<java.util.Map<String, Object>> resultList) {
        for (ChatList chat : chatList) {
            Map<String, Object> item = new java.util.HashMap<>();
            item.put("chatId", chat.getChatId());
            item.put("staffId", chat.getStaffId());
            // 查staff信息
            UserAccount staff = userAccountMapper.selectById(chat.getStaffId());
            item.put("staffName", staff != null ? staff.getUserName() : null);
            item.put("staffAvatar", staff != null ? staff.getAvatar() : null);
            item.put("staffStatus", staff != null ? staff.getOnlineStatus() : null);
            // 查最新消息
            QueryWrapper<MessageList> msgWrapper = new QueryWrapper<>();
            msgWrapper.eq("chat_id", chat.getChatId()).orderByDesc("create_time").last("limit 1");
            java.util.List<MessageList> msgList = messageListMapper.selectList(msgWrapper);
            if (!msgList.isEmpty()) {
                MessageList msg = msgList.get(0);
                item.put("lastMessageType", msg.getMessageType());
                item.put("lastMessage", msg.getContent());
                item.put("lastMessageTime", msg.getCreateTime() != null ? msg.getCreateTime().toString().replace("T", " ") : null);
            } else {
                item.put("lastMessageType", null);
                item.put("lastMessage", null);
                item.put("lastMessageTime", null);
            }
            resultList.add(item);
        }
    }

    @Override
    public int getChatList(Map<String, Object> data, java.util.List<java.util.Map<String, Object>> resultList) {
        if (data == null || data.get("runUser") == null) {
            return 1; // 参数不合法
        }
        String userId = data.get("runUser").toString();
        try {
            UserAccount user = userAccountMapper.selectById(userId);
            if (user == null) {
                return 2; // 用户不存在
            }
            // 查询chat_list
            QueryWrapper<ChatList> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", userId);
            java.util.List<ChatList> chatList = chatListMapper.selectList(wrapper);
            buildChatList(chatList, resultList);
            return 0;
        } catch (Exception e) {
            return 4; // 系统错误
        }
    }

    @Override
    public int getAllChatList(Map<String, Object> data, java.util.List<java.util.Map<String, Object>> resultList) {
        if (data == null || data.get("runUser") == null) {
            return 1; // 参数不合法
        }
        String userId = data.get("runUser").toString();
        try {
            UserAccount user = userAccountMapper.selectById(userId);
            if (user == null) {
                return 2; // 用户不存在
            }
            if (!"Admin".equals(user.getUserRole())) {
                return 3; // 操作不合法
            }
            // 查询所有chat_list
            java.util.List<ChatList> chatList = chatListMapper.selectList(null);
            buildChatList(chatList, resultList);
            return 0;
        } catch (Exception e) {
            return 4; // 系统错误
        }
    }

    @Override
    public int getAllMessages(Map<String, Object> data, java.util.List<java.util.Map<String, Object>> resultList) {
        if (data == null || data.get("runUser") == null || data.get("chatId") == null) {
            return 1; // 参数不合法
        }
        String userId = data.get("runUser").toString();
        String chatId = data.get("chatId").toString();
        try {
            UserAccount user = userAccountMapper.selectById(userId);
            if (user == null) {
                return 3; // 操作不合法
            }
            String role = user.getUserRole();
            if (!("User".equals(role) || "Admin".equals(role) || "Staff".equals(role))) {
                return 3; // 操作不合法
            }
            ChatList chat = chatListMapper.selectById(chatId);
            if (chat == null) {
                return 2; // 会话不存在
            }
            // 查询消息
            QueryWrapper<MessageList> msgWrapper = new QueryWrapper<>();
            msgWrapper.eq("chat_id", chatId).orderByAsc("create_time");
            java.util.List<MessageList> msgList = messageListMapper.selectList(msgWrapper);
            for (MessageList msg : msgList) {
                java.util.Map<String, Object> item = new java.util.HashMap<>();
                item.put("messageId", msg.getMessageId());
                item.put("senderId", msg.getSenderId());
                item.put("messageType", msg.getMessageType());
                item.put("content", msg.getContent());
                item.put("status", msg.getStatus());
                item.put("createTime", msg.getCreateTime() != null ? msg.getCreateTime().toString().replace("T", " ") : null);
                resultList.add(item);
            }
            return 0;
        } catch (Exception e) {
            return 4; // 系统错误
        }
    }

    @Override
    public int readChatMessages(Map<String, Object> data) {
        if (data == null || data.get("runUser") == null || data.get("chatId") == null) {
            return 1; // 参数不合法
        }
        String userId = data.get("runUser").toString();
        String chatId = data.get("chatId").toString();
        try {
            UserAccount user = userAccountMapper.selectById(userId);
            if (user == null) {
                return 3; // 操作不合法
            }
            String role = user.getUserRole();
            if (!("User".equals(role) || "Admin".equals(role) || "Staff".equals(role))) {
                return 3; // 操作不合法
            }
            ChatList chat = chatListMapper.selectById(chatId);
            if (chat == null) {
                return 2; // 会话不存在
            }
            // 批量更新消息状态
            QueryWrapper<MessageList> msgWrapper = new QueryWrapper<>();
            msgWrapper.eq("chat_id", chatId).ne("sender_id", userId);
            MessageList updateMsg = new MessageList();
            updateMsg.setStatus("Read");
            messageListMapper.update(updateMsg, msgWrapper);
            return 0;
        } catch (Exception e) {
            return 4; // 系统错误
        }
    }
} 