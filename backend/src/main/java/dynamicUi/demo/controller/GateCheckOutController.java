package dynamicUi.demo.controller;

import dynamicUi.demo.constant.Attribute;
import dynamicUi.demo.entity.GateCheckOut;
import dynamicUi.demo.entity.WorkflowStepType;
import dynamicUi.demo.repoistory.GateCheckOutRepository;
import dynamicUi.demo.service.WorkflowStepExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/gate-checkouts")
@RequiredArgsConstructor
public class GateCheckOutController {

    private final GateCheckOutRepository repository;
    private final WorkflowStepExecutor executor;

    @PostMapping
    public GateCheckOut checkOut(@RequestBody GateCheckOutRequest request) {
        GateCheckOut entity = GateCheckOut.builder()
                .gateNumber(request.gateNumber())
                .securityUser(request.securityUser())
                .remarks(request.remarks())
                .exitTime(LocalDateTime.now())
                .build();

        return executor.execute(request.jobOrderId(), WorkflowStepType.GATE_CHECK_OUT,
                repository, entity, GateCheckOut::setJobOrder);
    }

    @GetMapping("/{jobOrderId}")
    public GateCheckOut getByJobOrder(@PathVariable Long jobOrderId) {
        return repository.findByJobOrder_Id(jobOrderId).orElseThrow();
    }


    @GetMapping
    public List<?> getByStep(
            @RequestAttribute(
                    value = Attribute.SELECTED_FACILITY_ID,
                    required = false
            )
            String selectedFacilityId
    ) {
        return repository.findByJobOrderFacilityId(selectedFacilityId);
    }

    public record GateCheckOutRequest(Long jobOrderId, String gateNumber, String securityUser, String remarks) {}
}