package org.sevengod.javabe.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sevengod.javabe.entity.PersonalizedContents;
import org.sevengod.javabe.web.mapper.PersonalizedContentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public interface PersonalizedService extends IService<PersonalizedContents> {
    PersonalizedContents getByUserAndChapter (Long userId, Long chapterId);
    PersonalizedContents generateContent (Long userId, Long chapterId);
}
