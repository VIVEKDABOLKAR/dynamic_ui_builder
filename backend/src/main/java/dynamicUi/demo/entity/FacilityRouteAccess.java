package dynamicUi.demo.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Grants a facility visibility into a specific ui_route.
 *
 * `active` controls the grant: true = granted, false = revoked. Rows are
 * never deleted on revoke — this keeps a stable (facility_id, route_id)
 * identity so re-granting later is just a flip back to true, and keeps
 * a record of "this facility has been configured before" even after
 * everything on it gets revoked (see the fail-open note below).
 *
 * If a facility has ZERO rows here at all (not even inactive ones), it's
 * treated as "not yet configured" and navigation falls back to showing
 * everything (fail-open). Once a facility has at least one row (active or
 * not), only its active routes show.
 *
 * GLOBAL (and facilityId == null, e.g. no facility selected yet) is never
 * filtered here — that's handled in NavigationBuilderService. There is no
 * "GLOBAL" row in this table; the admin's "Global" option in Route Access
 * Management is a bulk write across every real facility, not a facility
 * of its own.
 */
@Entity
@Table(
        name = "facility_route_access",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"facility_id", "route_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityRouteAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "facility_id", nullable = false, length = 100)
    private String facilityId;

    @Column(name = "route_id", nullable = false)
    private Long routeId;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}