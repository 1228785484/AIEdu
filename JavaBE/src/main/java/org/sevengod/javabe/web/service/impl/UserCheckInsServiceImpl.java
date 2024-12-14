package org.sevengod.javabe.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sevengod.javabe.entity.UserCheckIns;
import org.sevengod.javabe.web.mapper.UserCheckInsMapper;
import org.sevengod.javabe.web.service.UserCheckInsService;
import org.sevengod.javabe.entity.context.CheckInContext;
import org.sevengod.javabe.handler.checkinsChain.StreakCheckInHandler;
import org.sevengod.javabe.handler.checkinsChain.PersistCheckInHandler;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

/**
 * 用户签到服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserCheckInsServiceImpl extends ServiceImpl<UserCheckInsMapper, UserCheckIns> implements UserCheckInsService {
    
    private final StreakCheckInHandler streakCheckInHandler;
    private final PersistCheckInHandler persistCheckInHandler;
    
    @PostConstruct
    public void init() {
        // 构建责任链：持久化 -> 连续签到统计（验证已整合到连续签到统计中）
        persistCheckInHandler.setNext(streakCheckInHandler);
    }
    
    @Override
    @Transactional
    public boolean checkIn(Long userId) {
        CheckInContext context = new CheckInContext(userId);
        persistCheckInHandler.handle(context);
        return context.isSuccess();
    }

    @Override
    public Long getCheckInCountBetweenDates(Long userId, LocalDate startDate, LocalDate endDate) {
        // 如果是同一个月，直接使用位图计数
        if (startDate.getYear() == endDate.getYear() && startDate.getMonthValue() == endDate.getMonthValue()) {
            Set<Integer> checkInDays = streakCheckInHandler.getMonthlyCheckInDays(userId, startDate.getYear(), startDate.getMonthValue());
            return (long) checkInDays.size();
        }

        // 跨月的情况，需要分月统计
        long count = 0;
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            Set<Integer> monthlyCheckIns = streakCheckInHandler.getMonthlyCheckInDays(
                userId, 
                currentDate.getYear(), 
                currentDate.getMonthValue()
            );
            
            // 如果是开始月份，只统计从开始日期到月末的签到
            if (currentDate.getYear() == startDate.getYear() && currentDate.getMonthValue() == startDate.getMonthValue()) {
                count += monthlyCheckIns.stream()
                    .filter(day -> day >= startDate.getDayOfMonth())
                    .count();
            }
            // 如果是结束月份，只统计从月初到结束日期的签到
            else if (currentDate.getYear() == endDate.getYear() && currentDate.getMonthValue() == endDate.getMonthValue()) {
                count += monthlyCheckIns.stream()
                    .filter(day -> day <= endDate.getDayOfMonth())
                    .count();
            }
            // 其他月份，统计整月签到
            else {
                count += monthlyCheckIns.size();
            }
            
            currentDate = currentDate.plusMonths(1).withDayOfMonth(1);
        }
        
        return count;
    }

    @Override
    public boolean isCheckedInOnDate(Long userId, LocalDate date) {
        return streakCheckInHandler.isCheckedIn(userId, date);
    }

    @Override
    public Set<Integer> getMonthlyCheckInDays(Long userId, int year, int month) {
        return streakCheckInHandler.getMonthlyCheckInDays(userId, year, month);
    }

    @Override
    public int getConsecutiveCheckInDays(Long userId) {
        return streakCheckInHandler.getConsecutiveStreak(userId);
    }
}
