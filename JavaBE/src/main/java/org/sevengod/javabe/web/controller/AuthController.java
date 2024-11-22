package org.sevengod.javabe.web.controller;

import lombok.RequiredArgsConstructor;
import org.sevengod.javabe.dto.LoginRequest;
import org.sevengod.javabe.dto.RegisterRequest;
import org.sevengod.javabe.web.service.AuthService.AuthService;
import org.sevengod.javabe.web.service.AuthService.EmailService;
import org.sevengod.javabe.web.service.UserService;
import org.sevengod.javabe.web.service.AuthService.VerificationCodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8080")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final VerificationCodeService verificationCodeService;
    private final EmailService emailService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/send-code")
    public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "邮箱地址不能为空"));
        }
        
        try {
            String code = verificationCodeService.generateCode(email);
            emailService.sendVerificationCode(email, code);
            return ResponseEntity.ok(Map.of("message", "验证码发送中，请注意查收"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "发送验证码失败: " + e.getMessage()));
        }
    }
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmailExists(@RequestParam String email) {
        boolean exists = userService.findByEmail(email) != null;
        return ResponseEntity.ok(Map.of("exists", exists));
    }
} 