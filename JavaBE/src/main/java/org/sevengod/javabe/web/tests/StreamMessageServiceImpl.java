package org.sevengod.javabe.web.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sevengod.javabe.entity.vo.StreamMessageRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class StreamMessageServiceImpl implements StreamMessageService {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String apiKey = "app-bwMsh8L2N5A3iSYqZKDw6wVr";
    private final String apiUrl = "https://dify.aipfuture.com/v1/chat-messages";

    private Mono<String> extractAnswer(String message) {
        return Mono.fromCallable(() -> {
            try {
                if (message == null || message.trim().isEmpty()) {
                    return null;
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
                        // 将答案包装成 SSE 格式
                        return "data: " + answer + "\n\n";
                    }
                }
                return null;
            } catch (Exception e) {
                log.error("Error processing message: {}", message, e);
                return null;
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<String> getAgentMessages() {
        return webClient.get()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class)
                .doOnNext(message -> log.debug("Received message: {}", message))
                .flatMap(this::extractAnswer)
                .filter(message -> message != null)
                .doOnNext(message -> log.debug("Sending message: {}", message))
                .onErrorResume(e -> {
                    log.error("Error in getAgentMessages", e);
                    return Mono.just("data: {\"error\": \"" + e.getMessage() + "\"}\n\n");
                });
    }

    @Override
    public Flux<String> sendStreamMessage(StreamMessageRequest request) {
        log.info("Sending stream message: {}", request);
        return webClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class)
                .doOnNext(message -> log.debug("Received raw message: {}", message))
                .concatMap(this::extractAnswer)  
                .filter(message -> message != null)
                .publishOn(Schedulers.boundedElastic())  
                .doOnNext(message -> log.debug("Sending formatted message: {}", message))
                .onErrorResume(e -> {
                    log.error("Error in sendStreamMessage", e);
                    return Flux.error(e);
                });
    }
}
