package dynamicUi.demo.repoistory;

import dynamicUi.demo.entity.AccessStatus;
import dynamicUi.demo.entity.UserFacilityAccess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserFacilityAccessRepository extends JpaRepository<UserFacilityAccess, Long> {
    List<UserFacilityAccess> findByUser_Username(String username);
    List<UserFacilityAccess> findByStatus(AccessStatus status);
}