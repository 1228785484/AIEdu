package org.sevengod.javabe.handler.checkinsChain;

import lombok.RequiredArgsConstructor;
import org.sevengod.javabe.entity.dto.UserCheckIns;
import org.sevengod.javabe.entity.context.CheckInContext;
import org.sevengod.javabe.web.mapper.UserCheckInsMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 持久化签到记录处理器
 */
@Component
@RequiredArgsConstructor
public class PersistCheckInHandler extends AbstractCheckInHandler {
    
    private final UserCheckInsMapper userCheckInsMapper;

    @Override
    protected void doHandle(CheckInContext context) {
        UserCheckIns checkIn = new UserCheckIns();
        checkIn.setUserId(context.getUserId());
        checkIn.setCheckInDate(LocalDate.now());
        checkIn.setCheckInTime(LocalDateTime.now());
        
        userCheckInsMapper.insert(checkIn);
    }
}
