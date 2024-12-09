package org.sevengod.javabe.web.controller;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.sevengod.javabe.web.service.StudyTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/study-time")
public class StudyTimeController {

    @Autowired
    private StudyTimeService studyTimeService;

    /**
     * 获取WebSocket连接信息
     * @param userId 用户ID
     * @param courseId 课程ID
     * @return WebSocket连接所需信息
     */
    @GetMapping("/ws-info")
    public JSONObject getWebSocketInfo(
            @RequestParam Long userId,
            @RequestParam Long courseId) {
        JSONObject result = new JSONObject();
        
        // 获取当天已学习时长
        Long todayStudyTime = studyTimeService.getDailyStudyTimeSeconds(
            userId, courseId, LocalDate.now());
        
        // 构建WebSocket连接URL（不包含/api前缀）
        String wsUrl = String.format("/study-time?userId=%d&courseId=%d", userId, courseId);
        
        result.put("wsUrl", wsUrl);
        result.put("todayStudyTime", todayStudyTime);
        
        log.info("User {} requested WebSocket connection info for course {}", userId, courseId);
        return result;
    }

    /**
     * 获取指定日期的学习时长
     */
    @GetMapping("/duration")
    public JSONObject getStudyTime(
            @RequestParam Long userId,
            @RequestParam Long courseId,
            @RequestParam(required = false) LocalDate date) {
        
        if (date == null) {
            date = LocalDate.now();
        }
        
        Long studyTime = studyTimeService.getDailyStudyTimeSeconds(userId, courseId, date);
        
        JSONObject result = new JSONObject();
        result.put("userId", userId);
        result.put("courseId", courseId);
        result.put("date", date);
        result.put("durationSeconds", studyTime);
        
        return result;
    }
}
