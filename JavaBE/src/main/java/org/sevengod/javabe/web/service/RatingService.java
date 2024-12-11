package org.sevengod.javabe.web.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface RatingService {
    Map<String, Object> getChapterRating(Long chapterId, Long userId);
}
