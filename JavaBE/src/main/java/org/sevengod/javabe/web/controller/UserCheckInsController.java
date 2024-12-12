package org.sevengod.javabe.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sevengod.javabe.common.AjaxResult;
import org.sevengod.javabe.web.service.UserCheckInsService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

/**
 * User Check-in Controller
 */
@RestController
@RequestMapping("/api/checkins")
@RequiredArgsConstructor
@Tag(name = "用户签到接口", description = "处理用户签到相关的请求")
public class UserCheckInsController {

    private final UserCheckInsService userCheckInsService;

    /**
     * User check in
     */
    @Operation(summary = "用户签到", description = "用户进行每日签到，每天只能签到一次")
    @Parameter(name = "userId", description = "用户ID", required = true)
    @PostMapping("/checkIn")
    public AjaxResult checkIn(@RequestParam("userId") Long userId) {
        if (userCheckInsService.checkIn(userId)) {
            return AjaxResult.success("签到成功");
        }
        return AjaxResult.error("你今天已经签到过了");
    }

    @PostMapping("/statistics")
    @Operation(summary = "获取签到统计",
            description = "获取用户在指定日期区间内的签到统计信息")
    public AjaxResult getCheckInStatistics(
            @Parameter(name = "request", description = "签到统计请求参数",
                    required = true,
                    schema = @Schema(example = "{\"userId\": 123, \"startDate\": \"2024-03-01\", \"endDate\": \"2024-03-31\"}"))
            @RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.parseLong(request.get("userId").toString());
            LocalDate startDate = LocalDate.parse(request.get("startDate").toString());
            LocalDate endDate = LocalDate.parse(request.get("endDate").toString());

            Long count = userCheckInsService.getCheckInCountBetweenDates(userId, startDate, endDate);
            return AjaxResult.success("获取签到统计成功", count);
        } catch (Exception e) {
            return AjaxResult.error("获取签到统计失败：" + e.getMessage());
        }
    }

    @PostMapping("/status")
    @Operation(summary = "查询签到状态",
            description = "查询用户指定日期的签到状态")
    public AjaxResult getCheckInStatus(
            @Parameter(name = "request", description = "签到状态查询参数",
                    required = true,
                    schema = @Schema(example = "{\"userId\": 123, \"date\": \"2024-03-20\"}"))
            @RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.parseLong(request.get("userId").toString());
            LocalDate date = LocalDate.parse(request.get("date").toString());

            boolean isCheckedIn = userCheckInsService.isCheckedInOnDate(userId, date);
            return AjaxResult.success("查询签到状态成功", isCheckedIn);
        } catch (Exception e) {
            return AjaxResult.error("查询签到状态失败：" + e.getMessage());
        }
    }

    @PostMapping("/monthly")
    @Operation(summary = "获取月度签到记录",
            description = "获取用户在指定年月的签到日期列表")
    public AjaxResult getMonthlyCheckIns(
            @Parameter(name = "request", description = "月度签到查询参数",
                    required = true,
                    schema = @Schema(example = "{\"userId\": 123, \"year\": 2024, \"month\": 3}"))
            @RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.parseLong(request.get("userId").toString());
            int year = Integer.parseInt(request.get("year").toString());
            int month = Integer.parseInt(request.get("month").toString());

            // 验证月份的有效性
            if (month < 1 || month > 12) {
                return AjaxResult.error("无效的月份值，月份必须在1-12之间");
            }

            Set<Integer> checkInDays = userCheckInsService.getMonthlyCheckInDays(userId, year, month);
            return AjaxResult.success("查询成功", checkInDays);
        } catch (NumberFormatException e) {
            return AjaxResult.error("参数格式错误");
        } catch (Exception e) {
            return AjaxResult.error("查询月度签到记录失败：" + e.getMessage());
        }
    }
}
