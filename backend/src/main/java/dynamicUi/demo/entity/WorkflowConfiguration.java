package dynamicUi.demo.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Ties a {@link WorkflowStep} into the live workflow with an execution
 * order and an active flag. This is what {@code JobOrderService} and
 * {@code JobStepService} read at runtime instead of the old hardcoded
 * {@code WorkflowStepType.values()} array — reordering or disabling a step
 * here takes effect on the next Job Order created without a deployment.
 */
@Entity
@Table(name = "workflow_configuration")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_step_id", nullable = false)
    private WorkflowStep workflowStep;

    // null = global/default workflow , we have facility_id for global as well take care this later
    @Column(name = "facility_id")
    private String facilityId;

    @Column(nullable = false)
    private Integer sequence;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}
