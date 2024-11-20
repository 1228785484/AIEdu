package org.sevengod.javabe.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sevengod.javabe.entity.Units;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UnitService extends IService<Units> {
    List<Units> findAll(int courseId);
}
