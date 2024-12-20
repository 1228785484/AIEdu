package org.sevengod.javabe.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sevengod.javabe.web.service.AIModelService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AI Model", description = "AI模型相关接口")
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIModelController {
    private final AIModelService aiModelService;

    @Operation(summary = "预测学习时间", description = "根据用户学习情况和课程难度预测完成学习所需时间")
    @GetMapping("/predict")
    public double predictLearnTime(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "课程ID") @RequestParam Long courseId,
            @Parameter(description = "难度等级：EASY(简单)、MEDIUM(中等)、HARD(困难)") 
            @RequestParam String difficulty
    ) {
        return aiModelService.predictLearnTime(userId, courseId, difficulty);
    }
}
