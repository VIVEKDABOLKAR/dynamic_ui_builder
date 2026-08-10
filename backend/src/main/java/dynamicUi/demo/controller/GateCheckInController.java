package dynamicUi.demo.controller;

import dynamicUi.demo.entity.GateCheckIn;
import dynamicUi.demo.entity.WorkflowStepType;
import dynamicUi.demo.repoistory.GateCheckInRepository;
import dynamicUi.demo.service.WorkflowStepExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gate-checkins")
@RequiredArgsConstructor
public class GateCheckInController {

    private final GateCheckInRepository repository;
    private final WorkflowStepExecutor executor;

    @PostMapping
    public GateCheckIn checkIn(@RequestBody GateCheckInRequest request) {
        GateCheckIn entity = GateCheckIn.builder()
                .gateNumber(request.gateNumber())
                .securityUser(request.securityUser())
                .truckNumber(request.truckNumber())
                .driverName(request.driverName())
                .remarks(request.remarks())
                .arrivalTime(java.time.LocalDateTime.now())
                .build();

        return executor.execute(request.jobOrderId(), WorkflowStepType.GATE_CHECK_IN,
                repository, entity, GateCheckIn::setJobOrder);
    }

    @GetMapping()
    public List<GateCheckIn> getAllGateCheckIn() {
        return repository.findAll();
    }

    @GetMapping("/{jobOrderId}")
    public GateCheckIn getByJobOrder(@PathVariable Long jobOrderId) {
        return repository.findByJobOrder_Id(jobOrderId).orElseThrow();
    }

    public record GateCheckInRequest(Long jobOrderId, String gateNumber, String securityUser, String truckNumber, String driverName, String remarks) {}
}