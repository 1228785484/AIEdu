package org.sevengod.javabe.dto;

import lombok.Data;

@Data
public class LoginRequest {
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
} 