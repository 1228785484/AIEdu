package org.sevengod.javabe.web.tests;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sevengod.javabe.entity.req.StreamMessageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
@Tag(name = "流式消息测试", description = "用于测试流式消息的接口")
@Slf4j
public class StreamMessageController {

    private final StreamMessageService streamMessageService;

    @GetMapping(value = "/messages")
    @Operation(summary = "获取消息流")
    public SseEmitter getMessages() {
        SseEmitter emitter = streamMessageService.getAgentMessages();
        
        // 设置完成、超时和错误的回调
        emitter.onCompletion(() -> streamMessageService.onCompletion(emitter));
        emitter.onTimeout(() -> streamMessageService.onTimeout(emitter));
        emitter.onError((ex) -> streamMessageService.onError(emitter, ex));
        
        return emitter;
    }

    @GetMapping(value = "/stream")
    @Operation(summary = "发送流式消息")
    public SseEmitter sendStreamMessage(
            @RequestParam String query,
            @RequestParam(defaultValue = "abc-123") String user) {
        
        // 创建请求对象
        StreamMessageRequest request = StreamMessageRequest.builder()
                .inputs(new java.util.HashMap<>())
                .query(query)
                .responseMode("streaming")
                .user(user)
                .build();
        
        SseEmitter emitter = streamMessageService.sendStreamMessage(request);
        
        // 设置完成、超时和错误的回调
        emitter.onCompletion(() -> streamMessageService.onCompletion(emitter));
        emitter.onTimeout(() -> streamMessageService.onTimeout(emitter));
        emitter.onError((ex) -> streamMessageService.onError(emitter, ex));
        
        return emitter;
    }
}
