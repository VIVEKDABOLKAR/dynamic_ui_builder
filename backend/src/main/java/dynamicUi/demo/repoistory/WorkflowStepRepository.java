package dynamicUi.demo.repoistory;

import dynamicUi.demo.entity.WorkflowStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkflowStepRepository extends JpaRepository<WorkflowStep, Long> {
    Optional<WorkflowStep> findByCode(String code);
    boolean existsByCode(String code);
}
