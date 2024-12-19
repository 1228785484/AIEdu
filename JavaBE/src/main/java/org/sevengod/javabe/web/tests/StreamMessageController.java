package org.sevengod.javabe.web.tests;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sevengod.javabe.entity.vo.StreamMessageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Tag(name = "流式消息测试", description = "用于测试流式消息的接口")
public class StreamMessageController {

    private final StreamMessageService streamMessageService;

    @GetMapping(value = "/messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "获取消息流")
    public Flux<String> getMessages() {
        return streamMessageService.getAgentMessages()
                .onErrorResume(e -> {
                    e.printStackTrace();
                    return Flux.empty();
                });
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "发送流式消息")
    public Flux<String> sendStreamMessage(@RequestBody StreamMessageRequest request) {
        return streamMessageService.sendStreamMessage(request)
                .onErrorResume(e -> {
                    e.printStackTrace();
                    return Flux.just("data: {\"error\": \"" + e.getMessage() + "\"}\n\n");
                });
    }
}
