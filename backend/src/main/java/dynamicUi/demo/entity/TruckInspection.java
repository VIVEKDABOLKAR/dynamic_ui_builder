package dynamicUi.demo.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "truck_inspection")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TruckInspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job-order_id",nullable = false)
    private JobOrder jobOrder;

    private LocalDateTime inspectionTime;

    private String tyreStatus;

    private String brakeStatus;

    private String inspectorUser;

    private String remarks;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
