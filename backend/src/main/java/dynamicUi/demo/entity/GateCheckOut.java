package dynamicUi.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "gate_check_out")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GateCheckOut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_order_id", nullable = false)
    private JobOrder jobOrder;

    @Column(name = "exit_time")
    private LocalDateTime exitTime;

    @Column(name = "gate_number")
    private String gateNumber;

    @Column(name = "security_user")
    private String securityUser;

    @Column(name = "remarks")
    private String remarks;

    @CreationTimestamp
    private LocalDateTime createdAt;
}