package org.sevengod.javabe.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.sevengod.javabe.common.AjaxResult;
import org.sevengod.javabe.web.service.StudyTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/study-time")
@Tag(name = "学习时长管理", description = "学习时长相关操作接口")
public class StudyTimeController {

    @Autowired
    private StudyTimeService studyTimeService;

    @Operation(summary = "获取WebSocket连接信息", description = "获取WebSocket连接URL和当天学习时长")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "500", description = "服务器错误")
    })
    @GetMapping("/ws-info")
    public AjaxResult getWebSocketInfo(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "课程ID", required = true) @RequestParam Long courseId) {
        
        // 获取当天已学习时长
        Long todayStudyTime = studyTimeService.getDailyStudyTimeSeconds(
            userId, courseId, LocalDate.now());
        
        // 构建WebSocket连接URL（不包含/api前缀）
        String wsUrl = String.format("/study-time?userId=%d&courseId=%d", userId, courseId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("wsUrl", wsUrl);
        data.put("todayStudyTime", todayStudyTime);
        
        log.info("User {} requested WebSocket connection info for course {}", userId, courseId);
        return AjaxResult.success("获取WebSocket连接信息成功", data);
    }

    @Operation(summary = "获取指定日期的学习时长", description = "获取用户在特定课程和日期的学习时长")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "500", description = "服务器错误")
    })
    @GetMapping("/duration")
    public AjaxResult getStudyTime(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "课程ID", required = true) @RequestParam Long courseId,
            @Parameter(description = "日期（可选，默认为当天）") @RequestParam(required = false) LocalDate date) {
        
        if (date == null) {
            date = LocalDate.now();
        }
        
        Long studyTime = studyTimeService.getDailyStudyTimeSeconds(userId, courseId, date);
        
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("courseId", courseId);
        data.put("date", date);
        data.put("durationSeconds", studyTime);
        
        return AjaxResult.success("获取学习时长成功", data);
    }

    @Operation(summary = "获取日期范围内的学习时长", description = "获取用户在特定课程在指定日期范围内的总学习时长")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "500", description = "服务器错误")
    })
    @GetMapping("/duration/range")
    public AjaxResult getStudyTimeByRange(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "课程ID", required = true) @RequestParam Long courseId,
            @Parameter(description = "开始日期", required = true) @RequestParam LocalDate startDate,
            @Parameter(description = "结束日期", required = true) @RequestParam LocalDate endDate) {
        
        Long studyTime = studyTimeService.getStudyTimeByRange(userId, courseId, startDate, endDate);
        
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("courseId", courseId);
        data.put("startDate", startDate);
        data.put("endDate", endDate);
        data.put("totalDurationSeconds", studyTime);
        
        return AjaxResult.success("获取日期范围内学习时长成功", data);
    }

    @Operation(summary = "获取课程总学习时长", description = "获取用户在特定课程的总学习时长")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "500", description = "服务器错误")
    })
    @GetMapping("/duration/total")
    public AjaxResult getTotalCourseStudyTime(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "课程ID", required = true) @RequestParam Long courseId) {
        
        Long studyTime = studyTimeService.getCourseStudyTimeSeconds(userId, courseId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("courseId", courseId);
        data.put("totalDurationSeconds", studyTime);
        
        return AjaxResult.success("获取课程总学习时长成功", data);
    }
}
