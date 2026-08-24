package dynamicUi.demo.repoistory;

import dynamicUi.demo.entity.JobStep;
import dynamicUi.demo.entity.WorkflowStepType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobStepRepository extends JpaRepository<JobStep, Long> {
    List<JobStep> findByJobOrder_IdOrderBySequenceNoAsc(Long jobOrderId);
    Optional<JobStep> findByJobOrder_IdAndStep(Long jobOrderId, WorkflowStepType step);
    Optional<JobStep> findFirstByJobOrder_IdAndSequenceNoGreaterThanOrderBySequenceNoAsc(
            Long jobOrderId,
            Integer sequenceNo
    );
}