package org.sevengod.javabe.entity.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProgressRequestVo {
    private Long chapterId;
    private Long enrollmentId;
    private BigDecimal percentage;
} 