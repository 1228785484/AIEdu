package org.sevengod.javabe.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.sevengod.javabe.entity.dto.Roles;
import org.sevengod.javabe.web.mapper.RolesMapper;
import org.sevengod.javabe.web.service.RolesService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RolesServiceImpl extends ServiceImpl<RolesMapper, Roles> implements RolesService {
    @Override
    public boolean assignTeacherRole(Long userId) {
        Roles teacherRole = new Roles();
        teacherRole.setUserId(userId);
        teacherRole.setRoleName("teacher");
        teacherRole.setAssignedAt(LocalDateTime.now());
        return this.save(teacherRole);
    }

    @Override
    public boolean assignStudentRole(Long userId) {
        Roles studentRole = new Roles();
        studentRole.setUserId(userId);
        studentRole.setRoleName("student");
        studentRole.setAssignedAt(LocalDateTime.now());
        return this.save(studentRole);
    }
}
