package org.sevengod.javabe.web.service.AuthService;

import lombok.RequiredArgsConstructor;
import org.sevengod.javabe.dto.LoginRequest;
import org.sevengod.javabe.dto.RegisterRequest;
import org.sevengod.javabe.entity.dto.User;
import org.sevengod.javabe.web.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserService userService;
    private final VerificationCodeService verificationCodeService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public ResponseEntity<?> register(RegisterRequest request) {
        // 验证验证码
        if (!verificationCodeService.verifyCode(request.getEmail(), request.getVerificationCode())) {
            return ResponseEntity.badRequest().body(Map.of("message", "验证码无效或已过期"));
        }

        // 检查用户名是否已存在
        if (userService.findByUsername(request.getUsername()) != null) {
            return ResponseEntity.badRequest().body(Map.of("message", "用户名已存在"));
        }

        // 检查邮箱是否已被注册
        if (userService.findByEmail(request.getEmail()) != null) {
            return ResponseEntity.badRequest().body(Map.of("message", "该邮箱已被注册"));
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setIsActive(true);

        userService.save(user);

        return ResponseEntity.ok(Map.of("message", "注册成功"));
    }

    public ResponseEntity<?> login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );

            User user = userService.findByUsername(request.getUsername());
            String token = jwtService.generateToken(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("userid",user.getUserId());
            
            return ResponseEntity.ok(response);
            
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "用户名或密码错误"));
        }
    }
} 