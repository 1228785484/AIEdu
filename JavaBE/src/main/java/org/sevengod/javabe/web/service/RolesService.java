package org.sevengod.javabe.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sevengod.javabe.entity.dto.Roles;

public interface RolesService extends IService<Roles> {
    boolean assignTeacherRole(Long userId);
    boolean assignStudentRole(Long userId);
    boolean checkRoleExists(Long userId);
    Roles getByUserId(Long userId);
}
