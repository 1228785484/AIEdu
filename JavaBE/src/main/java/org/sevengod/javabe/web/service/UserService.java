package org.sevengod.javabe.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.sevengod.javabe.entity.User;

public interface UserService extends IService<User> {
    User findByUsername(String username);
    User findByEmail(String email);
} 