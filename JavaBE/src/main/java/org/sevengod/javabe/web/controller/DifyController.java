package org.sevengod.javabe.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sevengod.javabe.common.AjaxResult;
import org.sevengod.javabe.entity.PersonalizedContents;
import org.sevengod.javabe.entity.resp.BlockResponse;
import org.sevengod.javabe.entity.resp.StreamResponse;
import org.sevengod.javabe.web.service.DifyService;
import org.sevengod.javabe.web.service.PersonalizedService;
import org.sevengod.javabe.web.service.QuizzesService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Tag(name = "Dify AI 控制器", description = "与Dify AI服务交互的API")

public class DifyController {

    @Value("${dify.key.ask-ai}")
    private String testKey;

    private final DifyService difyService;

    private final QuizzesService quizzesService;

    private final PersonalizedService personalizedService;

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

    @PostMapping("/genContent")
    @Operation(summary = "生成个性化内容")
    public AjaxResult genContent(
            @Parameter(name = "request", description = "包含用户ID和章节ID的请求体",
                    required = true,
                    schema = @Schema(example = "{\"userId\": 123, \"chapterId\": 123}"))
            @RequestBody Map<String, Long> request) {
        try {
            Long userId = request.get("userId");
            Long chapterId = request.get("chapterId");
            
            if (userId == null || chapterId == null) {
                return AjaxResult.error("用户ID和章节ID不能为空");
            }

            PersonalizedContents content = personalizedService.generateContent(userId, chapterId);
            return AjaxResult.success("生成内容成功", content);
            
        } catch (Exception e) {
            return AjaxResult.error("生成内容失败：" + e.getMessage());
        }
    }
    @PostMapping("/genQuiz")
    @Operation(summary = "生成个性化题目")
    public AjaxResult genQuiz(
            @Parameter(name = "request", description = "包含用户ID和章节ID的请求体",
                    required = true,
                    schema = @Schema(example = "{\"userId\": 123, \"chapterId\": 123}"))
            @RequestBody Map<String,Long> request){
        try {
            Long userId = request.get("userId");
            Long chapterId = request.get("chapterId");

            if (userId == null || chapterId == null) {
                return AjaxResult.error("用户ID和章节ID不能为空");
            }

            Map<String, Object> res = quizzesService.getQuizWithDifyResponse(chapterId, userId);
            return AjaxResult.success("生成内容成功", res);

        } catch (Exception e) {
            return AjaxResult.error("生成内容失败：" + e.getMessage());
        }
    }


}
