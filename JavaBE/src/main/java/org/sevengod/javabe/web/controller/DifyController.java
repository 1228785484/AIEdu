package org.sevengod.javabe.web.controller;

import lombok.RequiredArgsConstructor;
import org.sevengod.javabe.entity.resp.BlockResponse;
import org.sevengod.javabe.entity.resp.StreamResponse;
import org.sevengod.javabe.web.service.DifyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class DifyController {

    @Value("${dify.key.ask-ai}")
    private String testKey;

    private final DifyService difyService;

    @GetMapping("/block")
    public String test1() {
        String query = "鲁迅和周树人什么关系？";
        BlockResponse blockResponse = difyService.blockingMessage(query, 0L, testKey);
        return blockResponse.getAnswer();
    }

    @GetMapping("/stream")
    public Flux<StreamResponse> test2() {
        String query = "鲁迅和周树人什么关系？";
        return difyService.streamingMessage(query, 0L, testKey);
    }
//    @GetMapping("/askAi")
//    public String askAi(String query) {
//        BlockResponse blockResponse = difyService.blockingMessage(query, 0L, testKey);
//        return blockResponse.getAnswer();
//    }
    @PostMapping("/askAi")
    public BlockResponse askAi(@RequestBody String request) {
        return difyService.blockingMessage(
            request,
            1L,  // 使用默认用户ID
            testKey  // 使用默认API key
        );
    }
}
