package dynamicUi.demo.repoistory;

import dynamicUi.demo.entity.FacilityRouteAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FacilityRouteAccessRepository extends JpaRepository<FacilityRouteAccess, Long> {

    @Query("SELECT fra.routeId FROM FacilityRouteAccess fra WHERE fra.facilityId = :facilityId")
    List<Long> findRouteIdsByFacilityId(@Param("facilityId") String facilityId);

    boolean existsByFacilityId(String facilityId);

    boolean existsByFacilityIdAndRouteId(String facilityId, Long routeId);

}