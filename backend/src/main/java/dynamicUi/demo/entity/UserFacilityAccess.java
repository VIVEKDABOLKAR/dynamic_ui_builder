package dynamicUi.demo.entity;

import dynamicUi.demo.security.AppUser;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_facility_access")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFacilityAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "facility_id", nullable = false)
    private String facilityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AccessStatus status = AccessStatus.PENDING;
}