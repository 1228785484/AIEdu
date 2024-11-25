package org.sevengod.javabe.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sevengod.javabe.entity.Quizzes;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface QuizzesService extends IService<Quizzes> {
    // 获取单个测验并调用Dify生成响应
    Map<String, Object> getQuizWithDifyResponse(Long chapterId,Long userId);
}
