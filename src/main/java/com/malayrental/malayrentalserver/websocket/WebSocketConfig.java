package com.malayrental.malayrentalserver.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final WebSocketServer webSocketServer;
    private final UserTokenHandshakeInterceptor userTokenHandshakeInterceptor;

    public WebSocketConfig(WebSocketServer webSocketServer, UserTokenHandshakeInterceptor userTokenHandshakeInterceptor) {
        this.webSocketServer = webSocketServer;
        this.userTokenHandshakeInterceptor = userTokenHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketServer, "/ws")
                .addInterceptors(userTokenHandshakeInterceptor)
                .setAllowedOrigins("*");
    }
}