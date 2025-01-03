package org.sevengod.javabe.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sevengod.javabe.common.AjaxResult;
import org.sevengod.javabe.web.service.RatingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/rating")
@RequiredArgsConstructor
@Tag(name = "评分控制器", description = "处理章节评分相关的请求")
public class RatingController {
    
    private final RatingService ratingService;

    @PostMapping("/getChapterRating")
    @Operation(summary = "获取章节评分",
            description = "根据章节ID和用户ID获取AI生成的评分")
    public AjaxResult getChapterRating(
            @Parameter(name = "request", description = "获取章节评分请求体",
                    required = true,
                    schema = @Schema(example = "{\"chapterId\": 123, \"userId\": 456}"))
            @RequestBody Map<String, Object> request) {
        try {
            Long chapterId = Long.parseLong(request.get("chapterId").toString());
            Long userId = Long.parseLong(request.get("userId").toString());

            if (chapterId == null || userId == null) {
                return AjaxResult.error("章节ID和用户ID不能为空");
            }

            Map<String, Object> result = ratingService.getChapterRating(chapterId, userId);
            return AjaxResult.success("获取评分成功", result);

        } catch (Exception e) {
            return AjaxResult.error("获取评分失败：" + e.getMessage());
        }
    }

    @PostMapping("/getOverallRating")
    @Operation(summary = "获取课程总体评分",
            description = "根据课程ID和用户ID获取AI生成的总体评分")
    public AjaxResult getOverallRating(
            @Parameter(name = "request", description = "获取总体评分请求体",
                    required = true,
                    schema = @Schema(example = "{\"courseId\": 123, \"userId\": 456}"))
            @RequestBody Map<String, Object> request) {
        try {
            Long courseId = Long.parseLong(request.get("courseId").toString());
            Long userId = Long.parseLong(request.get("userId").toString());

            if (courseId == null || userId == null) {
                return AjaxResult.error("课程ID和用户ID不能为空");
            }

            Map<String, Object> result = ratingService.getOverallRating(userId, courseId);
            return AjaxResult.success("获取总体评分成功", result);

        } catch (Exception e) {
            return AjaxResult.error("获取总体评分失败：" + e.getMessage());
        }
    }
}
