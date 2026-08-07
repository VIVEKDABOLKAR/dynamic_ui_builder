package dynamicUi.demo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowConfigurationDTO {

    private Long id;

    private Long workflowStepId;
    private String workflowStepCode;
    private String workflowStepName;

    private Integer sequence;
    private boolean active;

    private String facilityId;
}
