package dynamicUi.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_order_number", unique = true)
    private String jobOrderNumber; // e.g. "JO-2026-000123"

    @Column(name = "facility_id", nullable = true)
    private String facilityId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "truck_number")
    private String truckNumber;

    @Column(name = "driver_name")
    private String driverName;

    @Column(name = "container_number")
    private String containerNumber;

    @Column(name = "appointment_time")
    private LocalDateTime appointmentTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_step")
    private WorkflowStepType currentStep;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private JobOrderStatus status = JobOrderStatus.CREATED;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}