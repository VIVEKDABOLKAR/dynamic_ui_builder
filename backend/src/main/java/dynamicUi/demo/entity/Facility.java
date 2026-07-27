package dynamicUi.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "facility")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facility {

    @Id
    @Column(name = "facility_id")
    private String id;

    @Column(nullable = false)
    private String name;
}