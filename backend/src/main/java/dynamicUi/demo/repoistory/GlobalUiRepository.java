package dynamicUi.demo.repoistory;

import dynamicUi.demo.entity.GlobalUiConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GlobalUiRepository
        extends JpaRepository<GlobalUiConfig,Long> {

    Optional<GlobalUiConfig>
    findByFacilityIdAndTypeAndIsActiveTrue(
            String facilityId,
            String type);

}
