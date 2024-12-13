package org.sevengod.javabe.web.controller.ZPTests;

import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.ClientV4;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/zhipu")
public class ZPController {
    private final ObjectMapper mapper = new ObjectMapper();
    private static final String requestIdTemplate = "test_request_%d";
    
    private String baseUrl="https://open.bigmodel.cn/api/paas/v4/chat/completions/";
    
    private String apiSecretKey="36ee4bf479a2510ea2e9e06475d8e42e.DC4mGPatYB1wftnK";
    
    private final ClientV4 client;
    
    public ZPController() {
        this.client = new ClientV4.Builder(baseUrl, apiSecretKey).build();
    }

    @PostMapping("/analyze-file")
    public ResponseEntity<?> analyzeFile(@RequestBody Map<String, String> request) {
        try {
            String fileUrl = request.get("fileUrl");
            if (fileUrl == null || fileUrl.isEmpty()) {
                return ResponseEntity.badRequest().body("文件URL不能为空");
            }

            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage chatMessage = new ChatMessage(
                ChatMessageRole.USER.value(), 
                "请分析这个文件的内容：" + fileUrl
            );
            messages.add(chatMessage);

            String requestId = String.format(requestIdTemplate, System.currentTimeMillis());
            
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model("glm-4v-flash")
                    .stream(Boolean.FALSE)
                    .messages(messages)
                    .requestId(requestId)
                    .invokeMethod(Constants.invokeMethod)
                    .build();

            ModelApiResponse response = client.invokeModelApi(chatCompletionRequest);
            return ResponseEntity.ok(mapper.writeValueAsString(response));

        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().body("处理响应失败：" + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("分析文件失败：" + e.getMessage());
        }
    }
}
