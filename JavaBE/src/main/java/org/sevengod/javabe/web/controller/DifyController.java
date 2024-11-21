package org.sevengod.javabe.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Dify AI 控制器", description = "与Dify AI服务交互的API")

public class DifyController {

    @Value("${dify.key.ask-ai}")
    private String testKey;

    private final DifyService difyService;

//    @GetMapping("/block")
//    public String test1() {
//        String query = "鲁迅和周树人什么关系？";
//        BlockResponse blockResponse = difyService.blockingMessage(query, 0L, testKey);
//        return blockResponse.getAnswer();
//    }

//    @GetMapping("/stream")
//    public Flux<StreamResponse> test2() {
//        String query = "鲁迅和周树人什么关系？";
//        return difyService.streamingMessage(query, 0L, testKey);
//    }
//    @GetMapping("/askAi")
//    public String askAi(String query) {
//        BlockResponse blockResponse = difyService.blockingMessage(query, 0L, testKey);
//        return blockResponse.getAnswer();
//    }
    @PostMapping("/askAi")
    @Operation(summary = "询问AI",
               description = "向AI发送查询并接收块响应",
               responses = {
                   @ApiResponse(responseCode = "200",
                                description = "AI响应成功",
                                content = @Content(schema = @Schema(implementation = BlockResponse.class))),
                   @ApiResponse(responseCode = "400", description = "请求错误"),
                   @ApiResponse(responseCode = "500", description = "服务器内部错误")
               })
    public BlockResponse askAi(
              @Parameter(description = "发送给AI的查询内容",
                   required = true,
                   schema = @Schema(type = "string"))
            @RequestBody String request) {
        return difyService.blockingMessage(
            request,
            1L,  // 使用默认用户ID
            testKey  // 使用默认API key
        );
    }
}
