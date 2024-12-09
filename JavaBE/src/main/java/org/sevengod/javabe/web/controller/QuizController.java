package org.sevengod.javabe.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sevengod.javabe.common.AjaxResult;
import org.sevengod.javabe.web.service.QuizzesService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/quiz")
@RequiredArgsConstructor
@Tag(name = "题目提交控制器", description = "与题目服务交互的API")
public class QuizController {
    private final QuizzesService quizzesService;


    @PostMapping("/submitQuiz")
    @Operation(
        summary = "提交测验答案",
        description = "提交用户的测验答案并计算得分，每个测验只能提交一次",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "提交成功",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        implementation = AjaxResult.class,
                        example = """
                            {
                                "code": 200,
                                "msg": "提交成功",
                                "data": {
                                    "submissionId": 1,
                                    "score": 85.5,
                                    "submittedAt": "2024-12-09T14:55:43"
                                }
                            }
                            """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "提交失败",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        implementation = AjaxResult.class,
                        example = """
                            {
                                "code": 400,
                                "msg": "提交失败：您已经提交过这个测验",
                                "data": null
                            }
                            """
                    )
                )
            )
        }
    )
    public AjaxResult submitQuiz(
            @Parameter(
                description = "测验提交请求体",
                required = true,
                schema = @Schema(
                    requiredProperties = {"quizId", "userId", "questions", "score", "timeLeft"},
                    example = """
                        {
                            "quizId": 123,
                            "userId": 456,
                            "questions": "[{\\\"id\\\": 1, \\\"question\\\": \\\"问题1\\\", \\\"answer\\\": \\\"答案1\\\"}]",
                            "score": 85.5,
                            "timeLeft": 300
                        }
                        """
                )
            )
            @RequestBody Map<String, Object> request) {
        try {
            Long quizId = Long.parseLong(request.get("quizId").toString());
            Long userId = Long.parseLong(request.get("userId").toString());
            String questions = request.get("questions").toString();
            Integer timeLeft = Integer.valueOf(request.get("timeLeft").toString());
            BigDecimal score = new BigDecimal(request.get("score").toString());

            if (quizId == null || userId == null) {
                return AjaxResult.error("测验ID和用户ID不能为空");
            }

            Map<String, Object> result = quizzesService.submitAnswerAndScore(quizId, userId, questions, score,timeLeft);
            return AjaxResult.success("提交成功", result);

        } catch (Exception e) {
            return AjaxResult.error("提交失败：" + e.getMessage());
        }
    }

    @PostMapping("/getUnitQuizScores")
    @Operation(summary = "获取单元测验分数",
            description = "获取用户在指定单元的所有章节测验分数")
    public AjaxResult getUnitQuizScores(
            @Parameter(name = "request", description = "获取单元测验分数请求体",
                    required = true,
                    schema = @Schema(example = "{\"unitId\": 123, \"userId\": 456}"))
            @RequestBody Map<String, Object> request) {
        try {
            Long unitId = Long.parseLong(request.get("unitId").toString());
            Long userId = Long.parseLong(request.get("userId").toString());

            if (unitId == null || userId == null) {
                return AjaxResult.error("单元ID和用户ID不能为空");
            }

            Map<String, Object> result = quizzesService.getUnitQuizScores(unitId, userId);
            return AjaxResult.success("获取成功", result);

        } catch (Exception e) {
            return AjaxResult.error("获取失败：" + e.getMessage());
        }
    }

    @PostMapping("/getChapterQuizScores")
    @Operation(summary = "获取章节测验分数",
            description = "获取用户在指定章节的测验分数")
    public AjaxResult getChapterQuizScores(
            @Parameter(name = "request", description = "获取单元测验分数请求体",
                    required = true,
                    schema = @Schema(example = "{\"chapterId\": 123, \"userId\": 456}"))
            @RequestBody Map<String, Object> request) {
        try {
            Long chapterId = Long.parseLong(request.get("chapterId").toString());
            Long userId = Long.parseLong(request.get("userId").toString());

            if (chapterId == null || userId == null) {
                return AjaxResult.error("章节ID和用户ID不能为空");
            }

            Map<String, Object> result = quizzesService.getChapterQuizScores(chapterId, userId);
            return AjaxResult.success("获取成功", result);

        } catch (Exception e) {
            return AjaxResult.error("获取失败：" + e.getMessage());
        }
    }
}
