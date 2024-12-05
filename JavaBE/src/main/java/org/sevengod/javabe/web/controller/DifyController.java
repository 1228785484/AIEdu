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
import org.sevengod.javabe.service.UserRequestLockService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Tag(name = "Dify AI 控制器", description = "与Dify AI服务交互的API")

public class DifyController {

    @Value("${dify.key.ask-ai}")
    private String testKey;

    private final DifyService difyService;

    private final UserRequestLockService lockService;

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
    public AjaxResult askAi(
            @Parameter(name = "request", description = "包含用户ID和用户输入的请求体",
                    required = true,
                    schema = @Schema(example = "{\"userId\": 123, \"query\": 用户输入内容}"))
            @RequestBody Map<String, Object> request
    ){
        try {
            Long userId = Long.valueOf((String)request.get("userId"));
            String query = (String) request.get("query");

            if (userId == null || query == null) {
                return AjaxResult.error("用户ID和查询ID不能为空");
            }
            if (!lockService.tryLock(String.valueOf(userId))) {
                return AjaxResult.error("请耐心等待AI回复");
            }
            try {
                BlockResponse response = difyService.blockingMessage(
                        query,
                        userId,
                        testKey
                );
                return AjaxResult.success("生成内容成功", response);
            } finally {
                // 确保锁一定会被释放
                lockService.unlock(String.valueOf(userId));
            }


        } catch (Exception e) {
            return AjaxResult.error("生成内容失败：" + e.getMessage());
        }
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

            CompletableFuture<PersonalizedContents> future = personalizedService.generateContent(userId, chapterId);
            PersonalizedContents content = future.get(30, TimeUnit.SECONDS);
            return AjaxResult.success("生成内容成功", content);
            
        } catch (TimeoutException e) {
            return AjaxResult.error("生成内容超时，请稍后重试");
        } catch (InterruptedException | ExecutionException e) {
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

            CompletableFuture<Map<String, Object>> res = quizzesService.getQuizWithDifyResponse(chapterId, userId);
            // 等待异步操作完成，设置30秒超时
            Map<String, Object> result = res.get(30, TimeUnit.SECONDS);
            return AjaxResult.success("生成内容成功", result);

        } catch (TimeoutException e) {
            return AjaxResult.error("生成内容超时，请稍后重试");
        } catch (InterruptedException | ExecutionException e) {
            return AjaxResult.error("生成内容失败：" + e.getMessage());
        }
    }


}
