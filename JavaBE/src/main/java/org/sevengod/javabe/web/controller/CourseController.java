package org.sevengod.javabe.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sevengod.javabe.common.AjaxResult;
import org.sevengod.javabe.dto.CourseBasicDTO;
import org.sevengod.javabe.entity.ChapterProgress;
import org.sevengod.javabe.entity.CourseEnrollment;
import org.sevengod.javabe.entity.TreeNode;
import org.sevengod.javabe.entity.Units;
import org.sevengod.javabe.entity.vo.EnrollRequestVo;
import org.sevengod.javabe.entity.vo.ProgressRequestVo;
import org.sevengod.javabe.entity.vo.StudyTimesUpdateRequestVo;
import org.sevengod.javabe.web.service.ChapterProgressService;
import org.sevengod.javabe.web.service.CourseService;
import org.sevengod.javabe.web.service.CourseTreeService;
import org.sevengod.javabe.web.service.EnrollmentService;
import org.sevengod.javabe.web.service.InfoBoardService;
import org.sevengod.javabe.web.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

//课程相关控制类
@RestController
@RequestMapping("/api/course")
@Tag(name = "课程管理控制器", description = "课程相关操作接口")
public class CourseController {
    @Autowired
    private UnitService unitService;
    @Autowired
    private CourseTreeService courseTreeService;
    @Autowired
    private EnrollmentService enrollmentService;
    @Autowired
    private ChapterProgressService chapterProgressService;
    @Autowired
    private InfoBoardService infoBoardService;
    @Autowired
    private CourseService courseService;

    @GetMapping("/list")
    @Operation(summary = "获取所有课程基本信息", description = "获取所有课程的ID、标题和描述信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取课程列表",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = CourseBasicDTO.class)))
    })
    public AjaxResult getAllCoursesBasic() {
        List<CourseBasicDTO> courses = courseService.getAllCoursesBasic();
        return AjaxResult.success(courses);
    }

    @GetMapping("/findUnits")
    @Operation(summary = "查询课程单元", description = "根据课程ID查询所有单元")
    @Parameters({
        @Parameter(name = "courseId", description = "课程ID", required = true, example = "1")
    })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查询成功",
            content = @Content(schema = @Schema(implementation = Units.class))),
        @ApiResponse(responseCode = "404", description = "课程未找到")
    })
    public List<Units> findAll(@RequestParam int courseId) {
        return unitService.findAll(courseId);
    }

    @GetMapping("/{courseId}/tree")
    @Operation(summary = "获取课程树结构", description = "根据课程ID获取完整的课程树")
    @Parameters({
        @Parameter(name = "courseId", description = "课程ID", required = true, example = "1")
    })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = TreeNode.class))),
        @ApiResponse(responseCode = "404", description = "课程未找到")
    })
    public ResponseEntity<List<TreeNode>> getCourseTree(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseTreeService.buildCourseTree(courseId));
    }

    @Operation(summary = "课程报名", description = "学生报名特定课程")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "报名成功",
            content = @Content(schema = @Schema(implementation = CourseEnrollment.class))),
        @ApiResponse(responseCode = "400", description = "报名失败")
    })
    @PostMapping("/enroll")
    public AjaxResult enrollCourse(@RequestBody EnrollRequestVo request) {
        try {
            CourseEnrollment enrollment = enrollmentService.enrollCourse(
                request.getUserId(),
                request.getCourseId()
            );
            return AjaxResult.success("选课成功", enrollment);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @GetMapping("/check-enrollment")
    @Operation(summary = "检查课程选课状态", description = "检查用户是否已经选过某个课程")
    @Parameters({
        @Parameter(name = "userId", description = "用户ID", required = true),
        @Parameter(name = "courseId", description = "课程ID", required = true)
    })
    public AjaxResult checkEnrollment(@RequestParam Long userId, @RequestParam Long courseId) {
        try {
            boolean isEnrolled = enrollmentService.isEnrolled(userId, courseId);
            return AjaxResult.success("查询成功", isEnrolled);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/updProgress")
    @Operation(summary = "更新课程进度", description = "更新学生在特定章节的学习进度")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "进度更新成功",
                    content = @Content(schema = @Schema(implementation = AjaxResult.class))),
            @ApiResponse(responseCode = "400", description = "更新失败")
    })
    public AjaxResult updateProgress(
            @Parameter(description = "进度更新请求体", required = true,
                    schema = @Schema(implementation = ProgressRequestVo.class))
            @RequestBody ProgressRequestVo request) {
        try {
            boolean success = chapterProgressService.updateProgress(
                request.getChapterId(),
                request.getEnrollmentId(),
                request.getPercentage()
            );
            if (success) {
                return AjaxResult.success("修改进度成功", true);
            } else {
                return AjaxResult.error("修改进度失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("修改进度失败：" + e.getMessage());
        }
    }

    @GetMapping("/getProgress")
    @Operation(summary = "获取课程进度", description = "获取学生在特定章节的学习进度")
    @Parameters({
            @Parameter(name = "chapterId", description = "章节ID", required = true,
                    schema = @Schema(type = "integer", format = "int64", example = "1")),
            @Parameter(name = "enrollmentId", description = "选课记录ID", required = true,
                    schema = @Schema(type = "integer", format = "int64", example = "1"))
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(schema = @Schema(implementation = ChapterProgress.class))),
            @ApiResponse(responseCode = "400", description = "获取失败"),
            @ApiResponse(responseCode = "404", description = "进度记录不存在")
    })
    public AjaxResult getProgress(@RequestParam Long chapterId,
                                  @RequestParam Long enrollmentId) {
        try {
            ChapterProgress progress = chapterProgressService.getProgress(chapterId, enrollmentId);
            if (progress != null) {
                return AjaxResult.success("获取进度成功", progress);
            } else {
                // 没有进度记录时返回空数据但不是错误
                return AjaxResult.success("暂无进度记录", null);
            }
        } catch (IllegalArgumentException e) {
            return AjaxResult.error("参数错误：" + e.getMessage());
        } catch (Exception e) {
            return AjaxResult.error("获取进度失败：" + e.getMessage());
        }
    }

    @GetMapping("/chapter-completion")
    @Operation(summary = "获取用户一个章节的完成状态", description = "检查用户是否完成了特定章节的测验和个性化内容(章节完成情况)")
    @Parameters({
        @Parameter(name = "userId", description = "用户ID", required = true,
                schema = @Schema(type = "integer", format = "int64", example = "1")),
        @Parameter(name = "chapterId", description = "章节ID", required = true,
                schema = @Schema(type = "integer", format = "int64", example = "1"))
    })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "400", description = "获取失败")
    })
    public AjaxResult getCompletionStatus(
            @RequestParam Long userId,
            @RequestParam Long chapterId) {
        try {
            boolean isComplete = infoBoardService.getCompleteStatus(userId, chapterId);
            return AjaxResult.success("获取完成状态成功", isComplete);
        } catch (Exception e) {
            return AjaxResult.error("获取完成状态失败：" + e.getMessage());
        }
    }

    @GetMapping("/unit-completion")
    @Operation(summary = "获取单元完成状态", description = "获取用户在指定单元下所有章节的完成情况统计(单元章节完成情况【任务点】)")
    @Parameters({
        @Parameter(name = "userId", description = "用户ID", required = true,
                schema = @Schema(type = "integer", format = "int64", example = "1")),
        @Parameter(name = "unitId", description = "单元ID", required = true,
                schema = @Schema(type = "integer", format = "int64", example = "1"))
    })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "400", description = "获取失败")
    })
    public AjaxResult getUnitCompletion(
            @RequestParam Long userId,
            @RequestParam Long unitId) {
        try {
            Map<String, Integer> stats = infoBoardService.getUnitCompletionStats(userId, unitId);
            return AjaxResult.success("获取单元完成情况成功", stats);
        } catch (Exception e) {
            return AjaxResult.error("获取单元完成情况失败：" + e.getMessage());
        }
    }

    @GetMapping("/quiz-completion")
    @Operation(summary = "获取单元测验完成情况", description = "获取用户在指定单元下的测验完成统计(测验)")
    @Parameters({
        @Parameter(name = "userId", description = "用户ID", required = true,
                schema = @Schema(type = "integer", format = "int64", example = "1")),
        @Parameter(name = "unitId", description = "单元ID", required = true,
                schema = @Schema(type = "integer", format = "int64", example = "1"))
    })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "400", description = "获取失败")
    })
    public AjaxResult getQuizCompletion(
            @RequestParam Long userId,
            @RequestParam Long unitId) {
        try {
            Map<String, Integer> stats = infoBoardService.getQuizCompletionStats(userId, unitId);
            return AjaxResult.success("获取测验完成情况成功", stats);
        } catch (Exception e) {
            return AjaxResult.error("获取测验完成情况失败：" + e.getMessage());
        }
    }

    @GetMapping("/course-completion-percentage")
    @Operation(summary = "获取课程完成百分比", description = "获取用户在指定课程的完成百分比(返回的是一个百分比的StringMap)")
    @Parameters({
        @Parameter(name = "userId", description = "用户ID", required = true,
                schema = @Schema(type = "integer", format = "int64", example = "1")),
        @Parameter(name = "courseId", description = "课程ID", required = true,
                schema = @Schema(type = "integer", format = "int64", example = "1"))
    })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "400", description = "获取失败")
    })
    public AjaxResult getCourseCompletionPercentage(
            @RequestParam Long userId,
            @RequestParam Long courseId) {
        try {
            Map<String, String> percentage = infoBoardService.getCourseCompletionPercentage(userId, courseId);
            return AjaxResult.success("获取课程完成百分比成功", percentage);
        } catch (Exception e) {
            return AjaxResult.error("获取课程完成百分比失败：" + e.getMessage());
        }
    }

    @PostMapping("/study-times-update")
    @Operation(summary = "更新学习次数", description = "更新用户每日学习次数(仅在章节完成时增加计数，24小时后自动清零)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "更新失败")
    })
    public AjaxResult updateStudyTimes(@RequestBody StudyTimesUpdateRequestVo request) {
        try {
            boolean updated = infoBoardService.studyTimesUpdate(
                    request.getUserId(),
                    request.getChapterId()
            );
            if (updated) {
                return AjaxResult.success("学习次数更新成功");
            } else {
                return AjaxResult.error("章节未完成，无法更新学习次数");
            }
        } catch (Exception e) {
            return AjaxResult.error("更新学习次数失败：" + e.getMessage());
        }
    }
}
