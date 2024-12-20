package org.sevengod.javabe.web.service;

import org.sevengod.javabe.enums.ChapterDifficulty;
import org.springframework.stereotype.Service;

public interface AIModelService {
    public double predictLearnTime(Long userId, Long courseId,String difficulty);

}
