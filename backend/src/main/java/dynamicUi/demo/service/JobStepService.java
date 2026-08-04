package dynamicUi.demo.service;

import dynamicUi.demo.entity.*;
import dynamicUi.demo.repoistory.JobOrderRepository;
import dynamicUi.demo.repoistory.JobStepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobStepService {

    private final JobStepRepository jobStepRepository;
    private final JobOrderRepository jobOrderRepository;

    private static final WorkflowStepType[] WORKFLOW = WorkflowStepType.values();

    /**
     * Marks the given step COMPLETED for a job order, then advances
     * currentStep to the next PENDING step (or marks the JobOrder
     * COMPLETED if this was the last step).
     */

    @Transactional
    public void completeStep(Long jobOrderId, WorkflowStepType step) {
        JobStep jobStep = jobStepRepository.findByJobOrder_IdAndStep(jobOrderId, step)
                .orElseThrow(() -> new RuntimeException("Step not found: " + step));

        jobStep.setStatus(JobStepStatus.COMPLETED);
        jobStep.setCompletedAt(LocalDateTime.now());
        jobStepRepository.save(jobStep);

        JobOrder jobOrder = jobOrderRepository.findById(jobOrderId).orElseThrow();

        int currentIndex = indexOf(step);
        int nextIndex = currentIndex + 1;

        if (nextIndex >= WORKFLOW.length) {
            jobOrder.setStatus(JobOrderStatus.COMPLETED);
            jobOrder.setCurrentStep(null);
        }
        else {
            WorkflowStepType nextStep = WORKFLOW[nextIndex];
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

    private int indexOf(WorkflowStepType step) {
        for (int i = 0; i < WORKFLOW.length; i++) {
            if (WORKFLOW[i] == step) return i;
        }
        throw new IllegalArgumentException("Unknown step: " + step);
    }
}