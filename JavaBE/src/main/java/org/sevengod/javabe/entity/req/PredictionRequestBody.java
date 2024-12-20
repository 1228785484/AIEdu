package org.sevengod.javabe.entity.req;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class PredictionRequestBody implements Serializable {
    @JSONField(name = "days",ordinal = 1)
    private Long days;

    @JSONField(name = "study_duration",ordinal = 2)
    private Double studyDuration;

    @JSONField(name = "completed_chapters",ordinal = 3)
    private Integer completedChapters;

    @JSONField(name = "difficulty_level",ordinal = 4)
    private Integer difficultyLevel;
}
    