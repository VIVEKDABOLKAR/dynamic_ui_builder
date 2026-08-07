package dynamicUi.demo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowConfigurationRequest {
    private Long workflowStepId;
    private Integer sequence;
    private boolean active;
}
