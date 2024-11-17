package org.sevengod.javabe.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;

@Service
public class VerificationCodeService {
    
    private final Map<String, CodeInfo> codeMap = new ConcurrentHashMap<>();
    
    public String generateCode(String email) {
        String code = String.format("%06d", new Random().nextInt(999999));
        codeMap.put(email, new CodeInfo(code, System.currentTimeMillis()));
        return code;
    }
    
    public boolean verifyCode(String email, String code) {
        CodeInfo codeInfo = codeMap.get(email);
        if (codeInfo == null) {
            return false;
        }
        
        // 验证码5分钟内有效
        boolean isValid = codeInfo.code.equals(code) 
            && (System.currentTimeMillis() - codeInfo.timestamp) <= 300000;
        
        if (isValid) {
            codeMap.remove(email); // 验证成功后删除验证码
        }
        
        return isValid;
    }
    
    private static class CodeInfo {
        String code;
        long timestamp;
        
        CodeInfo(String code, long timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }
    }
} 