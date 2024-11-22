package org.sevengod.javabe.web.service.AuthService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Async
    public void sendVerificationCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("AI教育平台 - 验证码");
        message.setText("您好！\n\n"
                + "您的验证码是：" + code + "\n\n"
                + "验证码有效期为5分钟，请尽快完成验证。\n\n"
                + "如果这不是您的操作，请忽略此邮件。\n\n"
                + "此邮件由系统自动发送，请勿回复。\n"
                + "© 2024 AI教育平台");
        
        mailSender.send(message);
    }
} 