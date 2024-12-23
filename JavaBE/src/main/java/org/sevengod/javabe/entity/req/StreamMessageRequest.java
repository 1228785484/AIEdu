package org.sevengod.javabe.entity.req;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StreamMessageRequest {
    @Builder.Default
    private Map<String, Object> inputs = new java.util.HashMap<>();
    
    private String query;
    
    @Builder.Default
    @JSONField(name = "response_mode")
    private String responseMode = "streaming";
    
    private String user;
}
