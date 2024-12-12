package org.sevengod.javabe.entity.context;

import lombok.Data;

/**
 * 签到上下文，用于在责任链中传递数据
 */
@Data
public class CheckInContext {
    private Long userId;
    private boolean isFirstCheckToday;
    private Integer streakDays;
    private boolean success;
    
    public CheckInContext(Long userId) {
        this.userId = userId;
        this.success = true;
    }
}
