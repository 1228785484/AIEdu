package org.sevengod.javabe.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class WorkflowResponse {
    private String event;
    
    @JSONField(name = "workflow_run_id")
    private String workflowRunId;
    
    @JSONField(name = "task_id")
    private String taskId;
    
    private WorkflowData data;

    @Data
    public static class WorkflowData {
        private String id;
        
        @JSONField(name = "workflow_id")
        private String workflowId;
        
        @JSONField(name = "sequence_number")
        private Integer sequenceNumber;
        
        private String status;
        private Map<String, Object> outputs;
        private String error;
        
        @JSONField(name = "elapsed_time")
        private Double elapsedTime;
        
        @JSONField(name = "total_tokens")
        private Integer totalTokens;
        
        @JSONField(name = "total_steps")
        private Integer totalSteps;
        
        @JSONField(name = "created_by")
        private CreatedBy createdBy;
        
        @JSONField(name = "created_at")
        private Long createdAt;
        
        @JSONField(name = "finished_at")
        private Long finishedAt;
        
        private List<Object> files;
    }

    @Data
    public static class CreatedBy {
        private String id;
        private String user;
    }
} 