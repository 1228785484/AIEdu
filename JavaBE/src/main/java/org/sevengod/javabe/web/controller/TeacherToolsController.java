package org.sevengod.javabe.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sevengod.javabe.common.AjaxResult;
import org.sevengod.javabe.web.service.LessonPlanService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "教师工具", description = "提供教案生成等教师辅助功能")
@RestController
@RequestMapping("/api/teacher/tools")
@RequiredArgsConstructor
public class TeacherToolsController {

    private final LessonPlanService lessonPlanService;

    @PostMapping("/generate-lesson-plan")
    @Operation(summary = "生成教案",
            description = "根据学生平均学习时长、平均分数和章节ID生成个性化教案")
    public AjaxResult generateLessonPlan(
            @Parameter(description = "请求参数", required = true,
                    schema = @Schema(example = """
                            {
                                "averageStudyDuration": 45,
                                "averageScore": 85.5,
                                "chapterId": 123
                            }
                            """))
            @RequestBody Map<String, Object> request) {
        try {
            // 参数验证和转换
            Integer averageStudyDuration = Integer.valueOf(request.get("averageStudyDuration").toString());
            Double averageScore = Double.valueOf(request.get("averageScore").toString());
            Long chapterId = Long.valueOf(request.get("chapterId").toString());

            // 参数校验
            if (averageStudyDuration <= 0) {
                return AjaxResult.error("平均学习时长必须大于0");
            }
            if (averageScore < 0 || averageScore > 100) {
                return AjaxResult.error("平均分数必须在0-100之间");
            }
            if (chapterId <= 0) {
                return AjaxResult.error("章节ID无效");
            }

            // 调用服务生成教案
            Map<String, Object> result = lessonPlanService.generateLessonPlan(
                    averageStudyDuration,
                    averageScore,
                    chapterId
            );

            return AjaxResult.success("教案生成成功", result);
        } catch (NumberFormatException e) {
            return AjaxResult.error("参数格式错误：" + e.getMessage());
        } catch (Exception e) {
            return AjaxResult.error("生成教案失败：" + e.getMessage());
        }
    }
}
