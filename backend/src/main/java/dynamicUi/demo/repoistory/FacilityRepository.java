package dynamicUi.demo.repoistory;

import dynamicUi.demo.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FacilityRepository extends JpaRepository<Facility, String> {

    List<Facility> findByIdIn(List<String> ids);

    @Query("""
        SELECT f FROM Facility f
        JOIN UserFacilityAccess ufa ON ufa.facilityId = f.id
        WHERE ufa.user.username = :username AND ufa.status = 'APPROVED'
        """)
    List<Facility> findAccessibleFacilitiesByUsername(@Param("username") String username);


}