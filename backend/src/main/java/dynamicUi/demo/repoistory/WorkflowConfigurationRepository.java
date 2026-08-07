package dynamicUi.demo.repoistory;

import dynamicUi.demo.entity.WorkflowConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkflowConfigurationRepository extends JpaRepository<WorkflowConfiguration, Long> {
    List<WorkflowConfiguration> findAllByOrderBySequenceAsc();
    List<WorkflowConfiguration> findByActiveTrueOrderBySequenceAsc();
    boolean existsByWorkflowStep_Id(Long workflowStepId);
    long countByActiveTrue();

    // Facility-scoped access. facilityId == null resolves to "global" rows
    // (Spring Data JPA translates a null equality parameter to IS NULL).
    List<WorkflowConfiguration> findByFacilityIdOrderBySequenceAsc(String facilityId);
    List<WorkflowConfiguration> findByFacilityIdAndActiveTrueOrderBySequenceAsc(String facilityId);
    boolean existsByFacilityId(String facilityId);
    boolean existsByWorkflowStep_IdAndFacilityId(Long workflowStepId, String facilityId);
}
