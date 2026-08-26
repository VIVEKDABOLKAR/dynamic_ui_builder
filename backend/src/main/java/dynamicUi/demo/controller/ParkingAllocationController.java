package dynamicUi.demo.controller;

import dynamicUi.demo.constant.Attribute;
import dynamicUi.demo.dto.JobStepDTO;
import dynamicUi.demo.entity.JobStep;
import dynamicUi.demo.entity.JobStepStatus;
import dynamicUi.demo.entity.ParkingAllocation;
import dynamicUi.demo.entity.WorkflowStepType;
import dynamicUi.demo.repoistory.JobStepRepository;
import dynamicUi.demo.repoistory.ParkingAllocationRepository;
import dynamicUi.demo.service.WorkflowStepExecutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking-allocations")
@RequiredArgsConstructor
public class ParkingAllocationController {

    private final ParkingAllocationRepository repository;
    private final JobStepRepository jobStepRepository;
    private final WorkflowStepExecutorService executor;

    @PostMapping
    public ParkingAllocation allocate(@RequestBody ParkingAllocationRequest request) {
        ParkingAllocation entity = ParkingAllocation.builder()
                .parkingSlot(request.parkingSlot())
                .assignedBy(request.assignedBy())
                .assignedTime(java.time.LocalDateTime.now())
                .build();

        return executor.execute(request.jobOrderId(), WorkflowStepType.PARKING_ALLOCATION,
                repository, entity, ParkingAllocation::setJobOrder);
    }

    @GetMapping
    public List<ParkingAllocation> getAllParkingAllocation(
            @RequestAttribute(value = Attribute.SELECTED_FACILITY_ID, required = false) String selectedFacilityId
    ) {
        return repository.findByJobOrderFacilityId(selectedFacilityId);
    }

    @GetMapping("/{jobOrderId}")
    public ParkingAllocation getByJobOrder(@PathVariable Long jobOrderId) {
        return repository.findByJobOrder_Id(jobOrderId).orElseThrow();
    }

    @GetMapping("/inprogress")
    public List<JobStepDTO> getAllInProgressParkingAllocation(
            @RequestAttribute(value = Attribute.SELECTED_FACILITY_ID, required = false) String selectedFacilityId
    ) {


        List<JobStep> jobStepList = jobStepRepository.findByJobOrder_FacilityIdAndStatusAndStep(selectedFacilityId, JobStepStatus.IN_PROGRESS, WorkflowStepType.PARKING_ALLOCATION);

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

    public record ParkingAllocationRequest(Long jobOrderId, String parkingSlot, String assignedBy) {}
}