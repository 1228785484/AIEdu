package org.sevengod.javabe.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.sevengod.javabe.web.service.StudyTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class StudyTimeHandler extends TextWebSocketHandler {
    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, StudySession> studySessions = new ConcurrentHashMap<>();

    @Autowired
    private StudyTimeService studyTimeService;

    private static class StudySession {
        Long userId;
        Long courseId;
        long startTimeSeconds;
        LocalDateTime lastHeartbeat;
        
        public StudySession(Long userId, Long courseId) {
            this.userId = userId;
            this.courseId = courseId;
            this.startTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
            this.lastHeartbeat = LocalDateTime.now();
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            String sessionId = session.getId();
            String query = session.getUri().getQuery();
            
            // 记录连接尝试的信息
            log.info("收到新的WebSocket连接尝试 - SessionID: {}, URI: {}, RemoteAddress: {}", 
                    sessionId, 
                    session.getUri(), 
                    session.getRemoteAddress());
            
            // Parse URL parameters
            Map<String, String> params = parseQueryString(query);
            if (!params.containsKey("userId") || !params.containsKey("courseId")) {
                log.error("Missing required URL parameters");
                session.close();
                return;
            }
            
            Long userId = Long.parseLong(params.get("userId"));
            Long courseId = Long.parseLong(params.get("courseId"));
            
            sessions.put(sessionId, session);
            studySessions.put(sessionId, new StudySession(userId, courseId));
            log.info("用户 {} 开始学习课程 {}", userId, courseId);
            
            // 发送连接成功消息
            JSONObject response = new JSONObject();
            response.put("type", "connected");
            response.put("message", "链接成功");
            sendMessage(session, new TextMessage(response.toString()));
        } catch (Exception e) {
            log.error("Error in connection establishment", e);
            session.close();
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String sessionId = session.getId();
        StudySession studySession = studySessions.get(sessionId);
        
        if (studySession == null) {
            log.error("No study session found for session ID: {}", sessionId);
            return;
        }
        
        try {
            // 使用Fastjson2解析消息
            JSONObject messageData = JSON.parseObject(message.getPayload());
            String type = messageData.getString("type");
            
            if ("heartbeat".equals(type)) {
                handleHeartbeat(sessionId, studySession);
                // 发送心跳响应
                JSONObject response = new JSONObject();
                response.put("type", "heartbeat_ack");
                sendMessage(session, new TextMessage(response.toString()));
            } else {
                log.warn("Unknown message type: {}", type);
            }
        } catch (Exception e) {
            log.error("Error handling message", e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String sessionId = session.getId();
        StudySession studySession = studySessions.get(sessionId);
        
        if (studySession != null) {
            try {
                // 计算本次学习时长（秒）
                long currentTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                long durationSeconds = currentTimeSeconds - studySession.startTimeSeconds;
                
                // 获取当天日期
                LocalDate today = LocalDate.now();
                
                // 更新当天的学习时长记录（秒）
                studyTimeService.updateDailyStudyTime(
                    studySession.userId,
                    studySession.courseId,
                    today,
                    durationSeconds
                );
                
                log.info("用户 {} 已停止学习课程 {}, 学习时长: {} 秒",
                    studySession.userId, studySession.courseId, durationSeconds);
                
            } catch (Exception e) {
                log.error("Error saving study time", e);
            }
        }
        
        // 清理会话数据
        sessions.remove(sessionId);
        studySessions.remove(sessionId);
    }

    private void handleHeartbeat(String sessionId, StudySession studySession) {
        studySession.lastHeartbeat = LocalDateTime.now();
    }
    
    private void sendMessage(WebSocketSession session, TextMessage message) {
        try {
            if (session.isOpen()) {
                session.sendMessage(message);
            }
        } catch (IOException e) {
            log.error("发送消息失败", e);
        }
    }

    // Helper method to parse query string
    private Map<String, String> parseQueryString(String query) {
        Map<String, String> params = new ConcurrentHashMap<>();
        if (query != null && !query.isEmpty()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return params;
    }
}