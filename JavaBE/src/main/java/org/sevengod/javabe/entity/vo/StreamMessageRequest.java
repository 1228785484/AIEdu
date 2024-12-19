package org.sevengod.javabe.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "流式消息请求体")
public class StreamMessageRequest {
    
    @Schema(description = "输入参数", example = "{}")
    private Map<String, Object> inputs;
    
    @Schema(description = "查询内容", example = "What are the specs of the iPhone 13 Pro Max?")
    private String query;
    
    @Schema(description = "响应模式", example = "streaming")
    private String response_mode;
    
    @Schema(description = "用户标识", example = "abc-123")
    private String user;
}
