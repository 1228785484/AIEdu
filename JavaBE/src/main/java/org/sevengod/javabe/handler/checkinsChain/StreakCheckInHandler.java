package org.sevengod.javabe.handler.checkinsChain;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RBitSet;
import org.redisson.api.RLock;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.sevengod.javabe.entity.context.CheckInContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 连续签到处理器 - 使用位图实现，并优化长期连续签到的处理
 */
@Component
@RequiredArgsConstructor
public class StreakCheckInHandler extends AbstractCheckInHandler {
    
    private final RedissonClient redisson;
    // 使用更清晰的键名格式
    private static final String CHECKIN_KEY = "user:%d:checkin:date:%d-%02d"; // user:checkin:userId:date:year-month
    private static final String STREAK_KEY = "user:%d:streak"; // user:streak:userId
    private static final String LAST_CHECKIN_KEY = "user:%d:last_checkin"; // user:last_checkin:userId
    private static final String LOCK_KEY = "user:%d:checkin:lock"; // user:checkin:lock:userId

    @Override
    protected void doHandle(CheckInContext context) {
        LocalDate today = LocalDate.now();
        
        // 验证是否已签到
        if (isCheckedIn(context.getUserId(), today)) {
            context.setSuccess(false);
            return;
        }
        
        // 设置首次签到标记
        context.setFirstCheckToday(true);
        
        // 更新签到状态
        updateCheckInStatus(context.getUserId(), today);
        context.setStreakDays(getConsecutiveStreak(context.getUserId()));
    }

    /**
     * 更新用户签到状态，包括位图记录和连续签到计数
     */
    private void updateCheckInStatus(Long userId, LocalDate date) {
        RLock lock = redisson.getLock(String.format(LOCK_KEY, userId));
        try {
            // 获取锁，最多等待5秒，10秒后自动释放
            if (!lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                throw new RuntimeException("获取锁失败");
            }

            // 1. 更新位图
            markCheckIn(userId, date);

            // 2. 获取上次签到日期
            RAtomicLong lastCheckIn = redisson.getAtomicLong(String.format(LAST_CHECKIN_KEY, userId));
            long lastCheckInEpochDay = lastCheckIn.get();
            LocalDate lastCheckInDate = lastCheckInEpochDay == 0 ? null : LocalDate.ofEpochDay(lastCheckInEpochDay);

            // 3. 更新连续签到计数
            RAtomicLong streak = redisson.getAtomicLong(String.format(STREAK_KEY, userId));
            
            if (lastCheckInDate == null) {
                // 第一次签到
                streak.set(1);
            } else {
                long daysBetween = ChronoUnit.DAYS.between(lastCheckInDate, date);
                if (daysBetween == 1) {
                    // 连续签到
                    streak.incrementAndGet();
                } else if (daysBetween == 0) {
                    // 当天重复签到，不做处理
                } else {
                    // 中断了连续签到
                    streak.set(1);
                }
            }

            // 4. 更新最后签到日期
            lastCheckIn.set(date.toEpochDay());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("处理签到状态时被中断", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 获取指定月份的位图，如果不存在则创建
     */
    private RBitSet getOrCreateMonthBitSet(Long userId, LocalDate date) {
        String key = String.format(CHECKIN_KEY, userId, date.getYear(), date.getMonthValue());
        RBitSet bitSet = redisson.getBitSet(key);
        
        if (!bitSet.isExists()) {
            // 初始化整月的位图
            int daysInMonth = date.lengthOfMonth();
            bitSet.clear();  // 确保位图为空
            bitSet.set(0, daysInMonth, false);
        }
        
        return bitSet;
    }

    /**
     * 标记用户在指定日期签到
     */
    private void markCheckIn(Long userId, LocalDate date) {
        RBitSet bitSet = getOrCreateMonthBitSet(userId, date);
        int dayIndex = date.getDayOfMonth() - 1;
        bitSet.set(dayIndex);
    }

    /**
     * 检查用户在指定日期是否签到
     */
    public boolean isCheckedIn(Long userId, LocalDate date) {
        RBitSet bitSet = getOrCreateMonthBitSet(userId, date);
        int dayIndex = date.getDayOfMonth() - 1;
        return bitSet.get(dayIndex);
    }

    /**
     * 获取用户当前的连续签到天数
     */
    public int getConsecutiveStreak(Long userId) {
        RAtomicLong streak = redisson.getAtomicLong(String.format(STREAK_KEY, userId));
        return (int) streak.get();
    }

    /**
     * 获取用户指定月份的签到记录
     */
    public Set<Integer> getMonthlyCheckInDays(Long userId, int year, int month) {
        LocalDate date = LocalDate.of(year, month, 1);
        RBitSet bitSet = getOrCreateMonthBitSet(userId, date);
        Set<Integer> checkInDays = new HashSet<>();
        
        int daysInMonth = date.lengthOfMonth();
        for (int day = 0; day < daysInMonth; day++) {
            if (bitSet.get(day)) {
                checkInDays.add(day + 1);
            }
        }
        
        return checkInDays;
    }
}
