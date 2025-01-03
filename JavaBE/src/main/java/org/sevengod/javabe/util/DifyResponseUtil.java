package org.sevengod.javabe.util;

import com.alibaba.fastjson2.JSON;
import org.sevengod.javabe.web.exception.DifyException;
import org.sevengod.javabe.web.service.DifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

public class DifyResponseUtil {
    private static final Logger log = LoggerFactory.getLogger(DifyResponseUtil.class);

    /**
     * 获取Dify AI的响应并处理
     *
     * @param difyService Dify服务实例
     * @param params     请求参数Map，需要包含Content和Type
     * @param userId     用户ID
     * @param apiKey     API密钥
     * @return 处理后的AI响应Map
     * @throws DifyException 如果处理过程中出现错误
     */
    public static Map<String, Object> getAIResponse(DifyService difyService, Map<String, String> params, String userId, String apiKey) throws DifyException {
        try {
            String aiResponse = Objects.requireNonNull(difyService.streamWorkflow(
                            params,
                            userId,
                            apiKey
                    )
                    .collectList()
                    .block(Duration.ofSeconds(30)))  // 设置30秒超时
                    .stream()
                    .filter(response -> "workflow_finished".equals(response.getEvent()))
                    .findFirst()
                    .<String>map(response -> {
                        return (String) response.getData().getOutputs().get("answer");
                    })
                    .orElseThrow(() -> new RuntimeException("未能获取到有效的生成内容"));

            // 清理JSON字符串
            aiResponse = aiResponse.replaceAll("```json\\n", "")
                    .replaceAll("```", "")
                    .trim();

            // 解析JSON数据并返回
            if(!JSON.isValidObject(aiResponse)){
                Map<String,Object> defaultResponse = Map.of("reply", aiResponse);
                return defaultResponse;
            }
            return JSON.parseObject(aiResponse);

        } catch (Exception e) {
            String errorDetails = "AI响应处理失败: " + e.getMessage();
            if (e instanceof TimeoutException) {
                throw DifyException.timeout();
            }
            throw new DifyException(errorDetails, e);
        }
    }
}
