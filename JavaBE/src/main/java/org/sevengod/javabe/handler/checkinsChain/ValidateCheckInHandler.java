package org.sevengod.javabe.handler.checkinsChain;

import lombok.RequiredArgsConstructor;
import org.sevengod.javabe.entity.UserCheckIns;
import org.sevengod.javabe.entity.context.CheckInContext;
import org.sevengod.javabe.web.mapper.UserCheckInsMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 验证签到处理器
 */
@Component
@RequiredArgsConstructor
public class ValidateCheckInHandler extends AbstractCheckInHandler {
    
    private final UserCheckInsMapper userCheckInsMapper;

    @Override
    protected void doHandle(CheckInContext context) {
        // 检查今日是否已签到
        UserCheckIns todayCheck = userCheckInsMapper.selectTodayCheckIn(context.getUserId(), LocalDate.now());
        
        if (todayCheck != null) {
            context.setSuccess(false);
            return;
        }
        
        context.setFirstCheckToday(true);
    }
}
