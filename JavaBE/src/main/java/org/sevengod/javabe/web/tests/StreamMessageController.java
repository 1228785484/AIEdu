package org.sevengod.javabe.web.tests;

import com.esotericsoftware.minlog.Log;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sevengod.javabe.entity.vo.StreamMessageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
@Tag(name = "流式消息测试", description = "用于测试流式消息的接口")
@Slf4j
public class StreamMessageController {

    private final StreamMessageService streamMessageService;

    @GetMapping(value = "/messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "获取消息流(没用)")
    public Flux<String> getMessages() {
        return streamMessageService.getAgentMessages()
                .onErrorResume(e -> {
                    log.error("Error in getMessages", e);
                    return Flux.error(e);
                });
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "发送流式消息")
    public Flux<String> sendStreamMessage(@RequestBody StreamMessageRequest request) {
        return streamMessageService.sendStreamMessage(request)
                .onErrorResume(e -> {
                    log.error("Error in sendStreamMessage", e);
                    return Flux.just("event: error\ndata: " + e.getMessage() + "\n\n");
                });
    }
}
