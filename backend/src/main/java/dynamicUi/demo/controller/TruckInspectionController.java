package dynamicUi.demo.controller;
import dynamicUi.demo.constant.Attribute;
import dynamicUi.demo.dto.JobStepDTO;
import dynamicUi.demo.entity.JobStep;
import dynamicUi.demo.entity.JobStepStatus;
import dynamicUi.demo.entity.TruckInspection;
import dynamicUi.demo.entity.WorkflowStepType;
import dynamicUi.demo.repoistory.JobStepRepository;
import dynamicUi.demo.repoistory.TruckInspectionRepository;
import dynamicUi.demo.service.WorkflowStepExecutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/truck-inspections")
@RequiredArgsConstructor
public class TruckInspectionController {

    private final TruckInspectionRepository repository;
    private final JobStepRepository jobStepRepository;
    private final WorkflowStepExecutorService executor;

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
    public List<TruckInspection> getAllTruckInspection(
            @RequestAttribute(value = Attribute.SELECTED_FACILITY_ID, required = false) String selectedFacilityId
    ) {
        return repository.findByJobOrderFacilityId(selectedFacilityId);
    }


    @GetMapping("/{jobOrderId}")
    public TruckInspection getByJobOrder(@PathVariable Long jobOrderId) {
        return repository.findByJobOrder_Id(jobOrderId).orElseThrow();
    }

    @GetMapping("/inprogress")
    public List<JobStepDTO> getAllInProgressTruckInspection(
            @RequestAttribute(value = Attribute.SELECTED_FACILITY_ID, required = false) String selectedFacilityId
    ) {

        List<JobStep> jobStepList = jobStepRepository.findByJobOrder_FacilityIdAndStatusAndStep(selectedFacilityId, JobStepStatus.IN_PROGRESS, WorkflowStepType.TRUCK_INSPECTION);

        return jobStepList.stream()
                .map(var ->
                        JobStepDTO.builder()
                                .id(var.getId())
                                .jobOrderId(var.getJobOrder().getId())
                                .status(var.getStatus())
                                .step(var.getStep())
                                .sequenceNo(var.getSequenceNo())
                                .build()
                )
                .toList();
    }

    public record TruckInspectionRequest(Long jobOrderId, String brakeStatus, String tyreStatus, String photoUrl, String inspectorUser, String remarks) {}
}