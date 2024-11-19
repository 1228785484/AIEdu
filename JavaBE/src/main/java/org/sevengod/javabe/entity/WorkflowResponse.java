package org.sevengod.javabe.entity;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class WorkflowResponse {
    private String event;
    private String workflowRunId;
    private String taskId;
    private WorkflowData data;

    @Data
    public static class WorkflowData {
        private String id;
        private String workflowId;
        private Integer sequenceNumber;
        private String status;
        private Map<String, Object> outputs;
        private String error;
        private Double elapsedTime;
        private Integer totalTokens;
        private Integer totalSteps;
        private CreatedBy createdBy;
        private Long createdAt;
        private Long finishedAt;
        private List<String> files;
    }

    @Data
    public static class CreatedBy {
        private String id;
        private String user;
    }
} 