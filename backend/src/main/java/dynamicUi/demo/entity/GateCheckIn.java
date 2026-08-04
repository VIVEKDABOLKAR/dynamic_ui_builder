package dynamicUi.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "gate_check_in")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GateCheckIn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_order_id", nullable = false)
    private JobOrder jobOrder;

    @Column(name = "arrival_time")
    private LocalDateTime arrivalTime;

    @Column(name = "gate_number")
    private String gateNumber;

    @Column(name = "security_user")
    private String securityUser; // username of the operator who checked the truck in

    @Column(name = "truck_number")
    private String truckNumber;

    @Column(name = "driver_name")
    private String driverName;

    @Column(name = "remarks")
    private String remarks;

    @CreationTimestamp
    private LocalDateTime createdAt;
}