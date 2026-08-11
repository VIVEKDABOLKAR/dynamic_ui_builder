package dynamicUi.demo.repoistory;

import dynamicUi.demo.entity.ParkingAllocation;
import dynamicUi.demo.entity.TruckInspection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.List;
import java.util.Optional;

public interface ParkingAllocationRepository extends JpaRepository<ParkingAllocation, Long> {
    Optional<ParkingAllocation> findByJobOrder_Id(Long jobOrderId);

    List<ParkingAllocation> findByJobOrderFacilityId(String facilityId);

    List<ParkingAllocation> findAll();
}