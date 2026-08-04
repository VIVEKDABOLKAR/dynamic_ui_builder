package dynamicUi.demo.service;

import dynamicUi.demo.entity.GateCheckIn;
import dynamicUi.demo.entity.JobOrder;
import dynamicUi.demo.entity.WorkflowStepType;
import dynamicUi.demo.repoistory.GateCheckInRepository;
import dynamicUi.demo.repoistory.JobOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GateCheckInService {

    private final GateCheckInRepository repository;
    private final JobOrderRepository jobOrderRepository;
    private final JobStepService jobStepService;

    @Transactional
    public GateCheckIn checkIn(Long jobOrderId, GateCheckIn request) {
        JobOrder jobOrder = jobOrderRepository.findById(jobOrderId)
                .orElseThrow(() -> new RuntimeException("Job order not found: " + jobOrderId));

        // Business rule:can only check in if this job order is actually at that step
        if (jobOrder.getCurrentStep() != WorkflowStepType.GATE_CHECK_IN) {
            throw new IllegalStateException(
                    "Job order is not at Gate Check-In step. Current step: " + jobOrder.getCurrentStep());
        }

        request.setJobOrder(jobOrder);
        if (request.getArrivalTime() == null) {
            request.setArrivalTime(java.time.LocalDateTime.now());
        }

        GateCheckIn saved = repository.save(request);

        // Mark this step complete and advance JobOrder to the next step (Truck Inspection)
        jobStepService.completeStep(jobOrderId, WorkflowStepType.GATE_CHECK_IN);

        return saved;
    }

    public GateCheckIn getByJobOrder(Long jobOrderId) {
        return repository.findByJobOrder_Id(jobOrderId)
                .orElseThrow(() -> new RuntimeException("No check-in found for job order: " + jobOrderId));
    }
}