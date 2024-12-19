package org.sevengod.javabe.web.tests;

import org.sevengod.javabe.entity.vo.StreamMessageRequest;
import reactor.core.publisher.Flux;

public interface StreamMessageService {
    Flux<String> getAgentMessages();
    
    /**
     * 发送流式消息请求
     * @param request 流式消息请求体
     * @return 流式响应
     */
    Flux<String> sendStreamMessage(StreamMessageRequest request);
}
