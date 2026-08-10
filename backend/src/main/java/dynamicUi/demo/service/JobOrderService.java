package dynamicUi.demo.service;

import dynamicUi.demo.entity.*;
import dynamicUi.demo.repoistory.JobOrderRepository;
import dynamicUi.demo.repoistory.JobStepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobOrderService {

    private final JobOrderRepository jobOrderRepository;
    private final JobStepRepository jobStepRepository;
    private final WorkflowConfigurationService workflowConfigurationService;

    @Transactional
    public JobOrder create(JobOrder jobOrder, String facilityId) {
        // Live, admin-configured workflow — no longer a fixed enum array.
        // Throws (409) if nothing is configured/active.
        List<WorkflowStepType> workflow = workflowConfigurationService.getActiveEffectiveStepsOrdered(facilityId);

        jobOrder.setStatus(JobOrderStatus.CREATED);
        jobOrder.setCurrentStep(workflow.getFirst());
        jobOrder.setFacilityId(facilityId);

        JobOrder saved = jobOrderRepository.save(jobOrder); // save first to get generated id

        String number = "JO-" + Year.now().getValue() + "-" + String.format("%06d", saved.getId());
        saved.setJobOrderNumber(number);
        jobOrderRepository.save(saved);

        // Auto-generate JobStep rows for the whole workflow
        for (int i = 0; i < workflow.size(); i++) {
            JobStep step = JobStep.builder()
                    .jobOrder(saved)
                    .step(workflow.get(i))
                    .sequenceNo(i + 1)
                    .status(i == 0 ? JobStepStatus.IN_PROGRESS : JobStepStatus.PENDING)
                    .build();
            jobStepRepository.save(step);
        }

        return saved;
    }

    public List<JobOrder> findByFacility(String facilityId) {
        return jobOrderRepository.findByFacilityId(facilityId);
    }

    public JobOrder findByNumber(String jobOrderNumber) {
        return jobOrderRepository.findByJobOrderNumber(jobOrderNumber)
                .orElseThrow(() -> new RuntimeException("Job order not found: " + jobOrderNumber));
    }

    public List<JobStep> getSteps(Long jobOrderId) {
        return jobStepRepository.findByJobOrder_IdOrderBySequenceNoAsc(jobOrderId);
    }

    public JobOrder cancel(Long id) {
        JobOrder jobOrder = jobOrderRepository.findById(id).orElseThrow();
        jobOrder.setStatus(JobOrderStatus.CANCELLED);
        return jobOrderRepository.save(jobOrder);
    }
}