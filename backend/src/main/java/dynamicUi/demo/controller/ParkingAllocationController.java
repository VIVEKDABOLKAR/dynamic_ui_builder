package dynamicUi.demo.controller;

import dynamicUi.demo.entity.ParkingAllocation;
import dynamicUi.demo.entity.WorkflowStepType;
import dynamicUi.demo.repoistory.ParkingAllocationRepository;
import dynamicUi.demo.service.WorkflowStepExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking-allocations")
@RequiredArgsConstructor
public class ParkingAllocationController {

    private final ParkingAllocationRepository repository;
    private final WorkflowStepExecutor executor;

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
    public List<ParkingAllocation> getAllParkingAllocation() {
        return repository.findAll();
    }

    @GetMapping("/{jobOrderId}")
    public ParkingAllocation getByJobOrder(@PathVariable Long jobOrderId) {
        return repository.findByJobOrder_Id(jobOrderId).orElseThrow();
    }

    public record ParkingAllocationRequest(Long jobOrderId, String parkingSlot, String assignedBy) {}
}