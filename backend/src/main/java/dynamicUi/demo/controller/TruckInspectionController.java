package dynamicUi.demo.controller;
import dynamicUi.demo.entity.TruckInspection;
import dynamicUi.demo.entity.WorkflowStepType;
import dynamicUi.demo.repoistory.TruckInspectionRepository;
import dynamicUi.demo.service.WorkflowStepExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/truck-inspections")
@RequiredArgsConstructor
public class TruckInspectionController {

    private final TruckInspectionRepository repository;
    private final WorkflowStepExecutor executor;

    @PostMapping
    public TruckInspection inspect(@RequestBody TruckInspectionRequest request) {
        TruckInspection entity = TruckInspection.builder()
                .brakeStatus(request.brakeStatus())
                .tyreStatus(request.tyreStatus())
                .inspectorUser(request.inspectorUser())
                .remarks(request.remarks())
                .inspectionTime(java.time.LocalDateTime.now())
                .build();

        return executor.execute(request.jobOrderId(), WorkflowStepType.TRUCK_INSPECTION,
                repository, entity, TruckInspection::setJobOrder);
    }


    @GetMapping
    public List<TruckInspection> getAllTruckInspection() {
        return repository.findAll();
    }

    @GetMapping("/{jobOrderId}")
    public TruckInspection getByJobOrder(@PathVariable Long jobOrderId) {
        return repository.findByJobOrder_Id(jobOrderId).orElseThrow();
    }

    public record TruckInspectionRequest(Long jobOrderId, String brakeStatus, String tyreStatus, String photoUrl, String inspectorUser, String remarks) {}
}