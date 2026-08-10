package dynamicUi.demo.repoistory;

import dynamicUi.demo.entity.FacilityRouteAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FacilityRouteAccessRepository extends JpaRepository<FacilityRouteAccess, Long> {

    @Query("SELECT fra.routeId FROM FacilityRouteAccess fra WHERE fra.facilityId = :facilityId")
    List<Long> findRouteIdsByFacilityId(@Param("facilityId") String facilityId);

    boolean existsByFacilityId(String facilityId);


    boolean existsByFacilityIdAndRouteIdAndActiveTrue(String facilityId, Long routeId);

    Optional<FacilityRouteAccess> findByFacilityIdAndRouteId(String facilityId, Long routeId);

    List<FacilityRouteAccess> findByFacilityId(String facilityId);

    List<FacilityRouteAccess> findByFacilityIdAndActiveTrue(String facilityId);

    @Query("""
    SELECT fra.routeId
    FROM FacilityRouteAccess fra
    WHERE fra.facilityId = :facilityId
      AND fra.active = true
""")
    List<Long> findRouteIdsByFacilityIdAndActiveTrue(String facilityId);
}