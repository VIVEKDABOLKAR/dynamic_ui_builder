package dynamicUi.demo.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * One permission pattern granted to a role, e.g.
 *   ROLE_ADMIN -> "*"
 *   ROLE_GATE_OPERATOR -> "gate.*"
 *
 * A role with ZERO rows here is treated as "not yet configured" and
 * AuthorizationService fails OPEN for it (mirrors FacilityRouteAccess's
 * fail-open behavior for facilities with no rows) — this keeps existing
 * pages working the moment this feature ships, until an admin actually
 * sets up patterns.
 */
@Entity
@Table(name = "role_permission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private AppRole appRole;

    @Column(name = "permission_pattern", nullable = false, length = 150)
    private String permissionPattern;
}
