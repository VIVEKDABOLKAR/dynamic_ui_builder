package dynamicUi.demo.service;

import dynamicUi.demo.entity.*;
import dynamicUi.demo.repoistory.JobOrderRepository;
import dynamicUi.demo.repoistory.JobStepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobStepService {

    private final JobStepRepository jobStepRepository;
    private final JobOrderRepository jobOrderRepository;
    private final WorkflowConfigurationService workflowConfigurationService;

    /**
     * Marks the given step COMPLETED for a job order, then advances
     * currentStep to the next PENDING step (or marks the JobOrder
     * COMPLETED if this was the last step).
     *
     * The workflow order is read from the live admin configuration on
     * every call rather than a fixed array, since it may have changed
     * since this Job Order was created — but note the JobStep rows
     * themselves were snapshotted at creation time (see JobOrderService),
     * so an in-flight job always finishes the workflow it started with as
     * long as those steps stay in the configuration.
     */
    @Transactional
    public void completeStep(Long jobOrderId, WorkflowStepType step) {
        JobStep jobStep = jobStepRepository.findByJobOrder_IdAndStep(jobOrderId, step)
                .orElseThrow(() -> new RuntimeException("Step not found: " + step));

        jobStep.setStatus(JobStepStatus.COMPLETED);
        jobStep.setCompletedAt(LocalDateTime.now());
        jobStepRepository.save(jobStep);

        JobOrder jobOrder = jobOrderRepository.findById(jobOrderId).orElseThrow();

        List<WorkflowStepType> workflow = workflowConfigurationService.getActiveEffectiveStepsOrdered(jobOrder.getFacilityId());
        int currentIndex = indexOf(workflow, step);
        int nextIndex = currentIndex + 1;

        if (currentIndex < 0 || nextIndex >= workflow.size()) {
            jobOrder.setStatus(JobOrderStatus.COMPLETED);
            jobOrder.setCurrentStep(null);
        }
        else {
            WorkflowStepType nextStep = workflow.get(nextIndex);
            jobOrder.setCurrentStep(nextStep);
            jobOrder.setStatus(JobOrderStatus.IN_PROGRESS);

            Optional<JobStep> nextJobStep = jobStepRepository.findByJobOrder_IdAndStep(jobOrderId, nextStep);
            nextJobStep.ifPresent(js -> {
                js.setStatus(JobStepStatus.IN_PROGRESS);
                jobStepRepository.save(js);
            });
        }

        jobOrderRepository.save(jobOrder);
    }

    private int indexOf(List<WorkflowStepType> workflow, WorkflowStepType step) {
        return workflow.indexOf(step);
    }
}