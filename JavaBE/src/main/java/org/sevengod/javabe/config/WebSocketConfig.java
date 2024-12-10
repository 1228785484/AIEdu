package org.sevengod.javabe.config;

import org.sevengod.javabe.config.properties.WebSocketProperties;
import org.sevengod.javabe.handler.StudyTimeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {


    @Autowired
    private StudyTimeHandler studyTimeHandler;

    @Autowired
    private WebSocketProperties webSocketProperties;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(studyTimeHandler, "/study-time")// 注册处理器
               .setAllowedOrigins("*");  // 允许所有来源，用于开发测试
    }
}