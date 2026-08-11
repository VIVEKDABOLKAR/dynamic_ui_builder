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
@Table(name = "dock_assignment")
@Builder
public class DockAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_order_id",nullable = false)
    private JobOrder jobOrder;

    private String dock;

    private LocalDateTime assignedTime;

    private String assignedBy;

    @CreationTimestamp
    private LocalDateTime createdAt;

}
