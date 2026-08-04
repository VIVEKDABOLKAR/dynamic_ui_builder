package dynamicUi.demo.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Grants a facility visibility into a specific ui_route.
 *
 * Presence of a row = granted. There is no "disabled" flag on purpose —
 * revoking access means deleting the row.
 *
 * If a facility has ZERO rows here at all, it's treated as "not yet
 * configured" and navigation falls back to showing everything (fail-open).
 * Once a facility has at least one row, only the routes listed for it show.
 *
 * GLOBAL (and facilityId == null, e.g. no facility selected yet) is never
 * filtered — that's handled in NavigationBuilderService, not here.
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
}