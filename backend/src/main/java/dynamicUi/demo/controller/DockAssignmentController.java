package dynamicUi.demo.controller;

import dynamicUi.demo.constant.Attribute;
import dynamicUi.demo.entity.DockAssignment;
import dynamicUi.demo.entity.WorkflowStepType;
import dynamicUi.demo.repoistory.DockAssignmentRepository;
import dynamicUi.demo.service.WorkflowStepExecutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/dock-assignments")
@RequiredArgsConstructor
public class DockAssignmentController {

    private final DockAssignmentRepository repository;
    private final WorkflowStepExecutorService executor;

    @PostMapping
    public DockAssignment assign(@RequestBody DockAssignmentRequest request){
        DockAssignment entity = DockAssignment.builder()
                .dock(request.dock())
                .assignedBy(request.assignedBy())
                .assignedTime(LocalDateTime.now())
                .build();
        return executor.execute(request.jobOrderId(), WorkflowStepType.DOCK_ASSIGNMENT,repository,entity,DockAssignment::setJobOrder);
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

    public record DockAssignmentRequest(Long jobOrderId, String dock, String assignedBy) {}


}
