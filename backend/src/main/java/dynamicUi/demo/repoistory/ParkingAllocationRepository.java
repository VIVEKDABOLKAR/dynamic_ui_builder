package dynamicUi.demo.repoistory;

import dynamicUi.demo.entity.ParkingAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParkingAllocationRepository extends JpaRepository<ParkingAllocation, Long> {
    Optional<ParkingAllocation> findByJobOrder_Id(Long jobOrderId);
}