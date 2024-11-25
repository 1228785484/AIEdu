//package org.sevengod.javabe.utils;
//
//import org.sevengod.javabe.entity.WorkflowResponse;
//
//import com.alibaba.fastjson2.JSON;
//
//import lombok.extern.slf4j.Slf4j;
//import reactor.core.publisher.Flux;
//
//@Slf4j
//public class SSEUtils {
//
//    public static Flux<WorkflowResponse> processSSE(String responseBody) {
//        return Flux.create(emitter -> {
//            try {
//                String[] chunks = responseBody.split("data:");
//                for (String chunk : chunks) {
//                    chunk = chunk.trim();
//                    if (!chunk.isEmpty()) {
//                        try {
//                            // 使用 Fastjson2 解析
//                            WorkflowResponse response = JSON.parseObject(chunk, WorkflowResponse.class);
//                            emitter.next(response);
//
//                            if ("workflow_finished".equals(response.getEvent())) {
//                                emitter.complete();
//                                return;
//                            }
//                        } catch (Exception e) {
//                            log.warn("解析SSE chunk失败: {}", chunk, e);
//                        }
//                    }
//                }
//                emitter.complete();
//            } catch (Exception e) {
//                emitter.error(e);
//            }
//        });
//    }
//}