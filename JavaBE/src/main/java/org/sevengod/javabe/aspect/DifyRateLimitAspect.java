package org.sevengod.javabe.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.sevengod.javabe.common.AjaxResult;
import org.sevengod.javabe.tasks.jobs.RedisGlobalBucketTask;
import org.sevengod.javabe.tasks.jobs.RedisLocalBucketTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
public class DifyRateLimitAspect {

    @Autowired
    private RedisGlobalBucketTask redisGlobalBucketTask;
    @Autowired
    private RedisLocalBucketTask redisLocalBucketTask;

    private static final int TOKENS_PER_REQUEST = 1; // 每个请求消耗的令牌数
    private static final long MAX_WAIT_TIME = 30000; // 最大等待时间（毫秒）

    @Around("execution(* org.sevengod.javabe.web.service.DifyService.*(..))")
    public Object aroundDifyService(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        // 获取参数名和值
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();
        
        // 查找userId参数
        String userId = null;
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getName().equals("userId") && args[i] != null) {
                userId = args[i].toString();
                break;
            }
        }

        // 如果找不到userId，只使用全局限流
        if (userId == null) {
            return handleGlobalRateLimit(joinPoint);
        }

        // 使用用户特定的限流 + 全局限流
        return handleCombinedRateLimit(joinPoint, userId);
    }

    private Object handleCombinedRateLimit(ProceedingJoinPoint joinPoint, String userId) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        while (System.currentTimeMillis() - startTime < MAX_WAIT_TIME) {
            // 检查用户限流和全局限流
            boolean userHasTokens = redisLocalBucketTask.tryConsume(userId, TOKENS_PER_REQUEST);
            if (!userHasTokens) {
                TimeUnit.MILLISECONDS.sleep(100);
                continue;
            }

            boolean globalHasTokens = redisGlobalBucketTask.tryAcquireTokens(TOKENS_PER_REQUEST);
            if (!globalHasTokens) {
                // 如果全局没有令牌，归还用户的令牌
                redisLocalBucketTask.returnTokens(userId, TOKENS_PER_REQUEST);
                TimeUnit.MILLISECONDS.sleep(100);
                continue;
            }

            // 两个限流都通过了
            try {
                return joinPoint.proceed();
            } catch (Exception e) {
                log.error("Dify service error for user {}", userId, e);
                throw e;
            }
        }
        
        // 如果超过最大等待时间仍未获取到令牌
        log.warn("Rate limit exceeded for user {} in Dify service", userId);
        return createErrorResponse("请求太频繁，请稍后再试");
    }

    private Object handleGlobalRateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        while (System.currentTimeMillis() - startTime < MAX_WAIT_TIME) {
            if (redisGlobalBucketTask.tryAcquireTokens(TOKENS_PER_REQUEST)) {
                try {
                    return joinPoint.proceed();
                } catch (Exception e) {
                    log.error("Dify service error ", e);
                    throw e;
                }
            }
            TimeUnit.MILLISECONDS.sleep(100);
        }
        
        log.warn("Global rate limit exceeded for Dify service");
        return createErrorResponse("系统繁忙，请稍后再试");
    }

    private Object createErrorResponse(String message) {
        return AjaxResult.error(message);
    }
}
