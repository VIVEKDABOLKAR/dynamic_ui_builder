package dynamicUi.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_step")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_order_id", nullable = false)
    private JobOrder jobOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkflowStepType step;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private JobStepStatus status = JobStepStatus.PENDING;

    @Column(name = "sequence_no", nullable = false)
    private Integer sequenceNo;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}