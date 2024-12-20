package org.sevengod.javabe.web.controller;

import lombok.RequiredArgsConstructor;
import org.sevengod.javabe.web.service.AIModelService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIModelController {
    private final AIModelService aiModelService;

    @GetMapping("/predict")
    public double predictLearnTime(
            @RequestParam Long userId,
            @RequestParam Long courseId,
            @RequestParam String difficulty
    ) {
        return aiModelService.predictLearnTime(userId, courseId, difficulty);
    }
}
