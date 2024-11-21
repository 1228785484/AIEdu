package org.sevengod.javabe.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sevengod.javabe.common.AjaxResult;
import org.sevengod.javabe.entity.CourseEnrollment;
import org.sevengod.javabe.entity.TreeNode;
import org.sevengod.javabe.entity.Units;
import org.sevengod.javabe.entity.vo.EnrollRequestVo;
import org.sevengod.javabe.web.service.CourseTreeService;
import org.sevengod.javabe.web.service.EnrollmentService;
import org.sevengod.javabe.web.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

}
