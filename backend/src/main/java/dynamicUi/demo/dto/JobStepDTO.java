package dynamicUi.demo.dto;

import dynamicUi.demo.entity.JobStepStatus;
import dynamicUi.demo.entity.WorkflowStepType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobStepDTO {

    private Long id;

    private Long jobOrderId;

    private WorkflowStepType step;

    private JobStepStatus status;

    private Integer sequenceNo;

}
