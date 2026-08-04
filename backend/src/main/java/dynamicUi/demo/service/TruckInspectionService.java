package dynamicUi.demo.service;

import dynamicUi.demo.entity.JobOrder;
import dynamicUi.demo.entity.TruckInspection;
import dynamicUi.demo.entity.WorkflowStepType;
import dynamicUi.demo.repoistory.JobOrderRepository;
import dynamicUi.demo.repoistory.TruckInspectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TruckInspectionService {
    private final TruckInspectionRepository repository;
    private final JobOrderRepository jobOrderRepository;
    private final JobStepService jobStepService;

    public TruckInspection inspect(TruckInspection request, Long jobOrderId) {
        JobOrder jobOrder = jobOrderRepository.findById(jobOrderId)
                .orElseThrow(() -> new RuntimeException("Job order Not Found:"+ jobOrderId));

        if(jobOrder.getCurrentStep() != WorkflowStepType.TRUCK_INSPECTION){
                throw  new IllegalStateException("job order is not at truck inspection,Current step:"+ jobOrder.getCurrentStep());
        }

        request.setJobOrder(jobOrder);
        request.setInspectionTime(LocalDateTime.now());

        TruckInspection saved = repository.save(request);
        jobStepService.completeStep(jobOrderId,WorkflowStepType.TRUCK_INSPECTION);

        return saved;

    }
}
