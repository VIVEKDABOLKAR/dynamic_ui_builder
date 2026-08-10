package dynamicUi.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_order_id",nullable = false)
    private JobOrder jobOrder;

    @Column(nullable = false)
    private String parkingSlot;

    @Column(nullable = false)
    private LocalDateTime assignedTime;

    private String assignedBy;

    @CreationTimestamp
    private String createdAt;

}
