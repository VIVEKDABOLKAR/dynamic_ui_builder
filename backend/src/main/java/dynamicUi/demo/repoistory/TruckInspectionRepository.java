package dynamicUi.demo.repoistory;


import dynamicUi.demo.entity.TruckInspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
public interface TruckInspectionRepository extends JpaRepository<TruckInspection, Long> {
    Optional<TruckInspection> findByJobOrder_Id(Long jobOrderId);

    List<TruckInspection> findByJobOrderFacilityId(String facilityId);
}
