package org.sevengod.javabe.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.sevengod.javabe.entity.dto.UserCheckIns;

import java.time.LocalDate;

@Mapper
public interface UserCheckInsMapper extends BaseMapper<UserCheckIns> {
    /**
     * 查询用户当天的签到记录
     */
    @Select("SELECT * FROM user_check_ins WHERE user_id = #{userId} AND check_in_date = #{date} LIMIT 1")
    UserCheckIns selectTodayCheckIn(@Param("userId") Long userId, @Param("date") LocalDate date);
}
