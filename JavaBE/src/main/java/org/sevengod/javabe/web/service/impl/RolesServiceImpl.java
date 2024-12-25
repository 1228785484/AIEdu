package org.sevengod.javabe.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
        if (checkRoleExists(userId)) {
            // Update existing role
            Roles teacherRole = this.getOne(new LambdaQueryWrapper<Roles>()
                    .eq(Roles::getUserId, userId));
            teacherRole.setRoleName("teacher");
            teacherRole.setAssignedAt(LocalDateTime.now());
            return this.updateById(teacherRole);
        } else {
            // Create new role
            Roles teacherRole = new Roles();
            teacherRole.setUserId(userId);
            teacherRole.setRoleName("teacher");
            teacherRole.setAssignedAt(LocalDateTime.now());
            return this.save(teacherRole);
        }
    }

    @Override
    public boolean assignStudentRole(Long userId) {
        if (checkRoleExists(userId)) {
            // Update existing role
            Roles studentRole = this.getOne(new LambdaQueryWrapper<Roles>()
                    .eq(Roles::getUserId, userId));
            studentRole.setRoleName("student");
            studentRole.setAssignedAt(LocalDateTime.now());
            return this.updateById(studentRole);
        } else {
            // Create new role
            Roles studentRole = new Roles();
            studentRole.setUserId(userId);
            studentRole.setRoleName("student");
            studentRole.setAssignedAt(LocalDateTime.now());
            return this.save(studentRole);
        }
    }

    @Override
    public boolean checkRoleExists(Long userId) {
        LambdaQueryWrapper<Roles> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Roles::getUserId, userId);
        return this.exists(wrapper);
    }

    @Override
    public Roles getByUserId(Long userId) {
        LambdaQueryWrapper<Roles> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Roles::getUserId, userId);
        return this.getOne(wrapper);
    }
}
