package com.malayrental.malayrentalserver.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.concurrent.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.malayrental.malayrentalserver.service.UserAccountService;
import com.malayrental.malayrentalserver.pojo.UserAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class WebSocketServer extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);
    private static final ConcurrentHashMap<String, WebSocketSession> userSessionMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> lastPingTimeMap = new ConcurrentHashMap<>();
    private final UserAccountService userAccountService;

    @Autowired
    public WebSocketServer(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::checkHeartbeats, 5, 5, TimeUnit.SECONDS);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = getUserId(session);
        String token = getToken(session);
        log.info("WebSocket连接建立，userId={}, token={}", userId, token);
        if (userId != null && token != null && checkUserToken(userId, token)) {
            userSessionMap.put(userId, session);
            lastPingTimeMap.put(userId, System.currentTimeMillis());
            log.info("WebSocket连接通过校验，userId={}", userId);
            userAccountService.updateUser(java.util.Map.of("userId", userId, "onlineStatus", "online"));
            log.info("用户已登录，userId={}", userId);
        } else {
            log.warn("WebSocket连接校验失败，userId={}, token={}", userId, token);
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("token校验失败"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = getUserId(session);
        log.info("WebSocket连接关闭，userId={}, status={}", userId, status);
        if (userId != null) {
            userSessionMap.remove(userId);
            lastPingTimeMap.remove(userId);
            userAccountService.logout(java.util.Map.of("runUser", userId));
            log.info("用户已登出，userId={}", userId);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String userId = getUserId(session);
        String payload = message.getPayload();
        log.debug("收到消息，userId={}, payload={}", userId, payload);
        if (userId == null) return;
        if ("ping".equalsIgnoreCase(payload)) {
            lastPingTimeMap.put(userId, System.currentTimeMillis());
            session.sendMessage(new TextMessage("pong"));
            log.debug("收到心跳ping，已回复pong，userId={}", userId);
        }
    }

    private void checkHeartbeats() {
        long now = System.currentTimeMillis();
        for (String userId : userSessionMap.keySet()) {
            Long lastPing = lastPingTimeMap.get(userId);
            if (lastPing == null || now - lastPing > 10_000) {
                WebSocketSession session = userSessionMap.get(userId);
                log.warn("心跳超时，关闭连接，userId={}", userId);
                if (session != null && session.isOpen()) {
                    try {
                        session.close(CloseStatus.SESSION_NOT_RELIABLE.withReason("心跳超时"));
                    } catch (Exception ignored) {}
                }
                userSessionMap.remove(userId);
                lastPingTimeMap.remove(userId);
                userAccountService.logout(java.util.Map.of("runUser", userId));
                log.info("用户已登出（心跳超时），userId={}", userId);
            }
        }
    }

    public void sendMessageToUser(String userId, String message) throws Exception {
        WebSocketSession session = userSessionMap.get(userId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        }
    }

    private String getUserId(WebSocketSession session) {
        Object userId = session.getAttributes().get("userId");
        return userId == null ? null : userId.toString();
    }

    private String getToken(WebSocketSession session) {
        Object token = session.getAttributes().get("token");
        return token == null ? null : token.toString();
    }

    private boolean checkUserToken(String userId, String token) {
        UserAccount user = userAccountService.getUserById(userId);
        return user != null
            && token.equals(user.getUserToken())
            && user.getTokenExpired() != null
            && user.getTokenExpired().isAfter(java.time.LocalDateTime.now());
    }
} 