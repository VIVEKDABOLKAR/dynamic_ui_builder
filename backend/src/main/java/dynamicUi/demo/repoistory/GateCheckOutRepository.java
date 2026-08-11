package dynamicUi.demo.repoistory;

import dynamicUi.demo.entity.GateCheckOut;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GateCheckOutRepository extends JpaRepository<GateCheckOut, Long> {
    Optional<GateCheckOut> findByJobOrder_Id(Long jobOrderId);

    List<?> findByJobOrderFacilityId(String selectedFacilityId);
}