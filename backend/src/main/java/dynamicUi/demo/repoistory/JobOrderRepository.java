package dynamicUi.demo.repoistory;

import dynamicUi.demo.entity.JobOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobOrderRepository extends JpaRepository<JobOrder, Long> {
    Optional<JobOrder> findByJobOrderNumber(String jobOrderNumber);
    List<JobOrder> findByFacilityId(String facilityId);
}