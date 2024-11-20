package org.sevengod.javabe.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.sevengod.javabe.entity.Units;
import org.sevengod.javabe.web.mapper.UnitMapper;
import org.sevengod.javabe.web.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitServiceImpl extends ServiceImpl<UnitMapper, Units> implements UnitService {
    @Autowired
    private UnitMapper unitMapper;
    @Override
    public List<Units> findAll(int courseId) {
        QueryWrapper<Units> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        return list(queryWrapper);
    }
}
