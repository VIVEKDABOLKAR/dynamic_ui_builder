package dynamicUi.demo.repoistory;

import dynamicUi.demo.entity.DockAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DockAssignmentRepository extends JpaRepository<DockAssignment,Long> {
    Optional<DockAssignment> findByJobOrder_Id(Long jobOrderId);

    List<DockAssignment> findByJobOrderFacilityId(String selectedFacilityId);
}
