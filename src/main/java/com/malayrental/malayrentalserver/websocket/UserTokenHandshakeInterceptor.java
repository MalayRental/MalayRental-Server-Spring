package com.malayrental.malayrentalserver.websocket;

import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.lang.NonNull;
import com.malayrental.malayrentalserver.service.UserAccountService;
import com.malayrental.malayrentalserver.pojo.UserAccount;
import org.springframework.stereotype.Component;

@Component
public class UserTokenHandshakeInterceptor implements HandshakeInterceptor {
    private final UserAccountService userAccountService;

    public UserTokenHandshakeInterceptor(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @Override
    public boolean beforeHandshake(
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler,
            @NonNull Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String userId = servletRequest.getServletRequest().getParameter("userId");
            String token = servletRequest.getServletRequest().getParameter("token");
            if (userId == null || token == null) {
                return false;
            }
            UserAccount user = userAccountService.getUserById(userId);
            if (user == null || !token.equals(user.getUserToken()) || user.getTokenExpired() == null || user.getTokenExpired().isBefore(LocalDateTime.now())) {
                return false;
            }
            attributes.put("userId", userId);
            attributes.put("token", token);
        }
        return true;
    }
    @Override
    public void afterHandshake(
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response,
            @NonNull WebSocketHandler wsHandler,
            Exception exception) {}
} 