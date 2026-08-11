package dynamicUi.demo.repoistory;

import dynamicUi.demo.entity.GateCheckIn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GateCheckInRepository extends JpaRepository<GateCheckIn, Long> {
    Optional<GateCheckIn> findByJobOrder_Id(Long jobOrderId);

    List<GateCheckIn> findByJobOrderFacilityId(String selectedFacilityId);
}