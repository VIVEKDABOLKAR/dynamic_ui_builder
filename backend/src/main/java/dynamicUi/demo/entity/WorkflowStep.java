package dynamicUi.demo.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Master data — every workflow step the system knows how to execute.
 * `code` must match a {@link WorkflowStepType} enum name, since the actual
 * step behaviour (gate check-in form, truck inspection form, etc.) is still
 * bound to that enum in the relevant services/controllers. This table only
 * controls which steps exist and lets an admin give them a friendly
 * name/description — inclusion/order/enable-disable lives in
 * {@link WorkflowConfiguration}.
 */
@Entity
@Table(name = "workflow_step")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String code; // e.g. "GATE_CHECK_IN" — must match WorkflowStepType

    @Column(nullable = false)
    private String name; // display name, e.g. "Gate Check In"

    private String description;
}
