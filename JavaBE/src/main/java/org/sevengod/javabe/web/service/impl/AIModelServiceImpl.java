package org.sevengod.javabe.web.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.esotericsoftware.minlog.Log;
import lombok.RequiredArgsConstructor;
import org.sevengod.javabe.entity.req.PredictionRequestBody;
import org.sevengod.javabe.web.service.*;
import org.springframework.stereotype.Service;
import org.sevengod.javabe.web.mapper.ChapterMapper;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.sevengod.javabe.enums.ChapterDifficulty;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AIModelServiceImpl implements AIModelService {
    private final UserCheckInsService userCheckInsService;
    private final StudyTimeService studyTimeService;
    private final InfoBoardService infoBoardService;
    private final ChapterMapper chapterMapper;
    private final RestTemplate restTemplate;

    private final String predictUrl="http://10.7.81.200:5000/predict";
    private final String authToken="393dc2fd-c2d2-4c31-b546-e21d24faf006";

    @Override
    public double predictLearnTime(Long userId, Long courseId, String difficulty) {
        // 获取签到天数 (从过去30天统计)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        Long days = userCheckInsService.getCheckInCountBetweenDates(userId, startDate, endDate);

        // 获取总学习时长（小时）
        Long totalStudyTimeSeconds = studyTimeService.getStudyTimeByRange(userId, null, startDate, endDate);
        double studyDurationHours = totalStudyTimeSeconds != null ? totalStudyTimeSeconds.doubleValue() / 3600.0 : 0.0;
        
        // 获取已完成章节数
        Integer completedChapters = infoBoardService.getCompletedChaptersCount(userId, courseId);

        // 构建请求体
        PredictionRequestBody requestBody = new PredictionRequestBody();
        requestBody.setDays(days);
        requestBody.setStudyDuration(studyDurationHours);
        requestBody.setCompletedChapters(completedChapters);
        requestBody.setDifficultyLevel(ChapterDifficulty.valueOf(difficulty).getValue());

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authToken);
        
        // 转换为JSON字符串，确保格式正确
        String jsonBody = JSON.toJSONString(requestBody);
        Log.info(jsonBody);
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);
        
        try {
            // 发送请求并获取预测结果
            String response = restTemplate.postForObject(predictUrl, requestEntity, String.class);
            JSONObject jsonObject = JSONObject.parseObject(response);
            return jsonObject.getJSONArray("prediction").getDouble(0);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}
