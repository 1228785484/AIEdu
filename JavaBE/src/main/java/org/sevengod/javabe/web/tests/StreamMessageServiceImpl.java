package org.sevengod.javabe.web.tests;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sevengod.javabe.entity.req.StreamMessageRequest;
import org.sevengod.javabe.entity.resp.StreamResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StreamMessageServiceImpl implements StreamMessageService {
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final String apiKey = "app-bwMsh8L2N5A3iSYqZKDw6wVr";
    private final String apiUrl = "https://dify.aipfuture.com/v1/chat-messages";
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final WebClient webClient = WebClient.create();

    private void processMessage(String message, SseEmitter emitter) {
        try {
            if (message == null || message.trim().isEmpty()) {
                return;
            }

            // 移除可能存在的 "data: " 前缀
            String content = message;
            if (content.startsWith("data: ")) {
                content = content.substring(6).trim();
            }

            JsonNode jsonNode = objectMapper.readTree(content);
            
            // 只处理 agent_message 事件
            if ("agent_message".equals(jsonNode.path("event").asText())) {
                String answer = jsonNode.path("answer").asText();
                if (answer != null && !answer.isEmpty()) {
                    // 发送 SSE 息
                    emitter.send(SseEmitter.event()
                            .data(answer)
                            .id(String.valueOf(System.currentTimeMillis()))
                            .name("message"));
                }
            }
        } catch (Exception e) {
            log.error("Error processing message: {}", message, e);
            try {
                emitter.send(SseEmitter.event()
                        .data("Error: " + e.getMessage())
                        .name("error"));
            } catch (IOException ex) {
                log.error("Error sending error message", ex);
            }
        }
    }

    @Override
    public SseEmitter getAgentMessages() {
        SseEmitter emitter = new SseEmitter(0L); // 无超时
        
        executorService.execute(() -> {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + apiKey);
                headers.setAccept(java.util.Collections.singletonList(MediaType.TEXT_EVENT_STREAM));
                
                ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
                );
                
                if (response.getBody() != null) {
                    processMessage(response.getBody(), emitter);
                }
                
                emitter.complete();
            } catch (Exception e) {
                log.error("Error in getAgentMessages", e);
                emitter.completeWithError(e);
            }
        });
        
        return emitter;
    }

    @Override
    public SseEmitter sendStreamMessage(StreamMessageRequest request) {
        SseEmitter emitter = new SseEmitter(0L); // 无超时
        
        executorService.execute(() -> {
            try {
                webClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .bodyValue(JSON.toJSONString(request))
                    .retrieve()
                    .bodyToFlux(StreamResponse.class)
                    .subscribe(
                        response -> {
                            try {
                                // 只处理 agent_message 事件
                                if ("agent_message".equals(response.getEvent())) {
                                    // 发送 SSE 事件
                                    emitter.send(SseEmitter.event()
                                        .id(response.getMessageId())
                                        .name("message")
                                        .data(response.getAnswer()));
                                }
                            } catch (IOException e) {
                                log.error("Error sending message", e);
                            }
                        },
                        error -> {
                            log.error("Error in stream", error);
                            emitter.completeWithError(error);
                        },
                        () -> {
                            log.info("Stream completed");
                            emitter.complete();
                        }
                    );
            } catch (Exception e) {
                log.error("Error in sendStreamMessage", e);
                emitter.completeWithError(e);
            }
        });
        
        return emitter;
    }

    @Override
    public void onCompletion(SseEmitter emitter) {
        try {
            emitter.complete();
        } catch (Exception e) {
            log.error("Error completing emitter", e);
        }
    }

    @Override
    public void onTimeout(SseEmitter emitter) {
        try {
            emitter.complete();
        } catch (Exception e) {
            log.error("Error on timeout", e);
        }
    }

    @Override
    public void onError(SseEmitter emitter, Throwable throwable) {
        try {
            emitter.completeWithError(throwable);
        } catch (Exception e) {
            log.error("Error handling error", e);
        }
    }
}
