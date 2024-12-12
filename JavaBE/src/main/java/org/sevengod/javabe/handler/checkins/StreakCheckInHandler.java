package org.sevengod.javabe.handler.checkins;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.sevengod.javabe.entity.context.CheckInContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 连续签到处理器
 */
@Component
@RequiredArgsConstructor
public class StreakCheckInHandler extends AbstractCheckInHandler {
    
    private final RedissonClient redisson;
    private static final String STREAK_KEY = "u:checkin:%d";

    @Override
    protected void doHandle(CheckInContext context) {
        String key = String.format(STREAK_KEY, context.getUserId());
        RBucket<Integer> bucket = redisson.getBucket(key);
        
        // 获取当前连续签到次数，不存在就是0
        int currentStreak = bucket.get() != null ? bucket.get() : 0;
        
        // 增加连续签到次数并刷新24小时
        currentStreak++;
        bucket.set(currentStreak, 24, TimeUnit.HOURS);
        
        context.setStreakDays(currentStreak);
    }
}
