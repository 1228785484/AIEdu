package org.sevengod.javabe.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sevengod.javabe.entity.UserCheckIns;

import java.time.LocalDate;
import java.util.Set;

public interface UserCheckInsService extends IService<UserCheckIns> {
    /**
     * User check in
     * @param userId user ID
     * @return true if check in successful, false if already checked in today
     */
    boolean checkIn(Long userId);

    /**
     * 获取用户在指定日期区间内的签到次数
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 签到次数
     */
    Long getCheckInCountBetweenDates(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * 判断用户在指定日期是否签到
     * @param userId 用户ID
     * @param date 指定日期
     * @return true 如果该日期已签到，false 如果未签到
     */
    boolean isCheckedInOnDate(Long userId, LocalDate date);

    /**
     * 获取用户指定月份的签到日期集合
     * @param userId 用户ID
     * @param year 年份
     * @param month 月份(1-12)
     * @return 该月份中已签到的日期集合
     */
    Set<Integer> getMonthlyCheckInDays(Long userId, int year, int month);
}
