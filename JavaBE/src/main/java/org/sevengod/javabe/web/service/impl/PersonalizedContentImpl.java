package org.sevengod.javabe.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.sevengod.javabe.entity.PersonalizedContents;
import org.sevengod.javabe.web.mapper.PersonalizedContentMapper;
import org.sevengod.javabe.web.service.CourseTreeService;
import org.sevengod.javabe.web.service.DifyService;
import org.sevengod.javabe.web.service.PersonalizedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
    //TODO 等Dify的接口完成制作
    @Override
    public PersonalizedContents generateContent(Long userId, Long chapterId) {
        if(getByUserAndChapter(userId, chapterId) == null) {
            //创建新的个性化内容
            PersonalizedContents newContent = new PersonalizedContents();
            newContent.setUserId(userId);
            newContent.setChapterId(chapterId);
            newContent.setContent(
                    difyService.blockingMessage(
                            courseTreeService.getChapterById(chapterId).getContentPrompt(),
                            userId,
                            APIKey
                    ).getAnswer()
            );
            newContent.setCreatedAt(LocalDateTime.now());
            newContent.setIsActive(true);
        }else{
            throw new RuntimeException("内容已生成过了");
        }
        return null;
    }
}
