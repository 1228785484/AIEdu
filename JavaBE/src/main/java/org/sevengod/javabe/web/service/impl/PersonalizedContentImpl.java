package org.sevengod.javabe.web.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.sevengod.javabe.entity.Chapter;
import org.sevengod.javabe.entity.PersonalizedContents;
import org.sevengod.javabe.entity.WorkflowResponse;
import org.sevengod.javabe.web.mapper.PersonalizedContentMapper;
import org.sevengod.javabe.web.service.CourseTreeService;
import org.sevengod.javabe.web.service.DifyService;
import org.sevengod.javabe.web.service.PersonalizedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PersonalizedContentImpl extends ServiceImpl<PersonalizedContentMapper, PersonalizedContents> implements PersonalizedService {
    @Autowired
    private PersonalizedContentMapper personalizedContentsMapper;
    @Autowired
    private DifyService difyService;
    @Autowired
    private CourseTreeService courseTreeService;
    private final String APIKey = "app-RjU3aR6XQ5Dd91QmeGF8UMoK";

    @Override
    public PersonalizedContents getByUserAndChapter(Long userId, Long chapterId) {
        QueryWrapper<PersonalizedContents> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("chapter_id", chapterId)
                   .eq("is_active", true);
        return personalizedContentsMapper.selectOne(queryWrapper);
    }
    @Override
    public PersonalizedContents generateContent(Long userId, Long chapterId) {
        try {
            // 参数验证
            if (userId == null || chapterId == null) {
                throw new IllegalArgumentException("用户ID和章节ID不能为空");
            }

            // 检查是否已存在内容
            PersonalizedContents existingContent = getByUserAndChapter(userId, chapterId);
            if (existingContent != null && existingContent.getIsActive()) {
                return existingContent;
            }

            // 获取章节提示语
            Chapter chapter = courseTreeService.getChapterById(chapterId);
            if (chapter == null) {
                throw new RuntimeException("未找到指定章节");
            }
            if (chapter.getContentPrompt() == null || chapter.getContentPrompt().trim().isEmpty()) {
                throw new RuntimeException("章节提示语为空");
            }

            // 调用Dify生成内容
            try {
                String generatedContent = difyService.streamWorkflow(
                        Map.of("Content", chapter.getContentPrompt(),
                                "Type", "内容"
                        ),
                        userId.toString(),
                        APIKey
                )
                .collectList()
                .block()  // 等待所有响应
                .stream()
                .filter(response -> "workflow_finished".equals(response.getEvent()))
                .findFirst()
                .<String>map(response -> {
                    return (String) response.getData().getOutputs().get("answer");
                })
                .orElseThrow(() -> new RuntimeException("未能获取到有效的生成内容"));

                // 验证生成的内容
                if (generatedContent == null || generatedContent.trim().isEmpty()) {
                    throw new RuntimeException("生成的内容为空");
                }

                // 创建新的个性化内容
                PersonalizedContents newContent = new PersonalizedContents();
                newContent.setUserId(userId);
                newContent.setChapterId(chapterId);
                newContent.setContent(generatedContent);
                newContent.setCreatedAt(LocalDateTime.now());
                newContent.setIsActive(true);

                // 保存到数据库
                personalizedContentsMapper.insert(newContent);
                return newContent;

            } catch (Exception e) {
                log.error("调用Dify服务失败: ", e);
                throw new RuntimeException("生成个性化内容失败: " + e.getMessage());
            }

        } catch (Exception e) {
            log.error("生成个性化内容失败: ", e);
            throw new RuntimeException("生成个性化内容失败: " + e.getMessage());
        }
    }
}
