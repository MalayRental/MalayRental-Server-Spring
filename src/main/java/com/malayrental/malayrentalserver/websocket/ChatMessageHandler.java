package com.malayrental.malayrentalserver.websocket;

import com.malayrental.malayrentalserver.dao.ChatListMapper;
import com.malayrental.malayrentalserver.dao.MessageListMapper;
import com.malayrental.malayrentalserver.dao.UserAccountMapper;
import com.malayrental.malayrentalserver.pojo.ChatList;
import com.malayrental.malayrentalserver.pojo.MessageList;
import com.malayrental.malayrentalserver.pojo.UserAccount;
import com.malayrental.malayrentalserver.common.IdGeneratorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;

@Component
public class ChatMessageHandler {
    private static final Logger log = LoggerFactory.getLogger(ChatMessageHandler.class);
    private final UserAccountMapper userAccountMapper;
    private final ChatListMapper chatListMapper;
    private final MessageListMapper messageListMapper;

    @Autowired
    public ChatMessageHandler(UserAccountMapper userAccountMapper, ChatListMapper chatListMapper, MessageListMapper messageListMapper) {
        this.userAccountMapper = userAccountMapper;
        this.chatListMapper = chatListMapper;
        this.messageListMapper = messageListMapper;
    }

    /**
     * 处理WebSocket消息
     */
    public ChatMessageResult handleMessage(String payload) {
        // 解析格式: [Request][ChatService][SendMessage][runUser][chatId][messageType]消息内容
        if (payload == null || !payload.startsWith("[Request][ChatService][SendMessage][")) {
            return new ChatMessageResult("[Response][400]未知命令！", null, null);
        }
        try {
            // 新的分割方式，保证字段准确
            String[] parts = payload.split("]", 6);
            if (parts.length < 6) {
                return new ChatMessageResult("[Response][400]参数不合法！", null, null);
            }
            String runUser = parts[3].substring(1);
            String chatId = parts[4].substring(1);
            String messageType = parts[5].substring(1, parts[5].indexOf("]"));
            String content = parts[5].substring(parts[5].indexOf("]") + 1);
            if (runUser.isEmpty() || chatId.isEmpty() || messageType.isEmpty() || content.isEmpty()) {
                return new ChatMessageResult("[Response][400]参数不合法！", null, null);
            }
            // 校验用户
            UserAccount user = userAccountMapper.selectById(runUser);
            if (user == null) {
                return new ChatMessageResult("[Response][400]操作不合法！", null, null);
            }
            String role = user.getUserRole();
            if (!("User".equals(role) || "Admin".equals(role) || "Staff".equals(role))) {
                return new ChatMessageResult("[Response][400]操作不合法！", null, null);
            }
            // 校验会话
            ChatList chat = chatListMapper.selectById(chatId);
            if (chat == null) {
                return new ChatMessageResult("[Response][400]会话不存在！", null, null);
            }
            // 检查是否为对话成员
            if (!(runUser.equals(chat.getUserId()) || runUser.equals(chat.getStaffId()))) {
                return new ChatMessageResult("[Response][400]非此对话成员！", null, null);
            }
            // 插入消息
            MessageList msg = new MessageList();
            msg.setMessageId(IdGeneratorUtil.generateId(messageListMapper, "message_id", "MES"));
            msg.setChatId(chatId);
            msg.setSenderId(runUser);
            msg.setMessageType(messageType);
            msg.setContent(content);
            msg.setStatus("Unread");
            msg.setCreateTime(LocalDateTime.now());
            int rows = messageListMapper.insert(msg);
            if (rows > 0) {
                // 推送给对方
                String targetUserId = runUser.equals(chat.getUserId()) ? chat.getStaffId() : chat.getUserId();
                String pushContent = "[Push][ChatService][NewMessage][" + chatId + "][" + messageType + "]" + content;
                return new ChatMessageResult("[Response][200]消息发送成功！", targetUserId, pushContent);
            } else {
                return new ChatMessageResult("[Response][400]参数不合法！", null, null);
            }
        } catch (Exception e) {
            log.error("消息处理异常", e);
            return new ChatMessageResult("[Response][400]参数不合法！", null, null);
        }
    }
} 