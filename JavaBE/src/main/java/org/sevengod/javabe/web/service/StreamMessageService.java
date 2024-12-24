package org.sevengod.javabe.web.service;

import org.sevengod.javabe.entity.req.StreamMessageRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface StreamMessageService {
    SseEmitter getAgentMessages();
    SseEmitter sendStreamMessage(StreamMessageRequest request);
    void onCompletion(SseEmitter emitter);
    void onTimeout(SseEmitter emitter);
    void onError(SseEmitter emitter, Throwable throwable);
}
