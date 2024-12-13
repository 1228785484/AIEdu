package org.sevengod.javabe.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sevengod.javabe.entity.UserCheckIns;
import org.sevengod.javabe.web.mapper.UserCheckInsMapper;
import org.sevengod.javabe.web.service.UserCheckInsService;
import org.sevengod.javabe.entity.context.CheckInContext;
import org.sevengod.javabe.handler.checkinsChain.ValidateCheckInHandler;
import org.sevengod.javabe.handler.checkinsChain.StreakCheckInHandler;
import org.sevengod.javabe.handler.checkinsChain.PersistCheckInHandler;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户签到服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserCheckInsServiceImpl extends ServiceImpl<UserCheckInsMapper, UserCheckIns> implements UserCheckInsService {
    
    private final ValidateCheckInHandler validateCheckInHandler;
    private final StreakCheckInHandler streakCheckInHandler;
    private final PersistCheckInHandler persistCheckInHandler;
    
    @PostConstruct
    public void init() {
        // 构建责任链：验证 -> 持久化 -> 连续签到统计
        validateCheckInHandler.setNext(persistCheckInHandler);
        persistCheckInHandler.setNext(streakCheckInHandler);
    }
    
    @Override
    @Transactional
    public boolean checkIn(Long userId) {
        CheckInContext context = new CheckInContext(userId);
        validateCheckInHandler.handle(context);
        return context.isSuccess();
    }

    @Override
    public Long getCheckInCountBetweenDates(Long userId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<UserCheckIns> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCheckIns::getUserId, userId)
                .ge(UserCheckIns::getCheckInDate, startDate)
                .le(UserCheckIns::getCheckInDate, endDate);
        
        return this.count(wrapper);
    }

    @Override
    public boolean isCheckedInOnDate(Long userId, LocalDate date) {
        LambdaQueryWrapper<UserCheckIns> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCheckIns::getUserId, userId)
                .eq(UserCheckIns::getCheckInDate, date);
        return this.count(wrapper) > 0;
    }

    @Override
    public Set<Integer> getMonthlyCheckInDays(Long userId, int year, int month) {
        // 构建月份的起始和结束日期
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        // 创建查询条件
        LambdaQueryWrapper<UserCheckIns> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCheckIns::getUserId, userId)
                .ge(UserCheckIns::getCheckInDate, startDate)
                .le(UserCheckIns::getCheckInDate, endDate)
                .select(UserCheckIns::getCheckInDate); // 只查询签到日期字段，优化性能

        // 查询并转换结果
        return this.list(wrapper).stream()
                .map(checkIn -> checkIn.getCheckInDate().getDayOfMonth())
                .collect(Collectors.toSet());
    }

    @Override
    public int getConsecutiveCheckInDays(Long userId) {
        // 通过StreakCheckInHandler获取连续签到天数
        return streakCheckInHandler.getConsecutiveStreak(userId);
    }
}
