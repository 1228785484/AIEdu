package org.sevengod.javabe.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sevengod.javabe.entity.dto.PersonalizedContents;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
public interface PersonalizedService extends IService<PersonalizedContents> {
    PersonalizedContents getByUserAndChapter (Long userId, Long chapterId);
    CompletableFuture<PersonalizedContents> generateContent (Long userId, Long chapterId);

}
