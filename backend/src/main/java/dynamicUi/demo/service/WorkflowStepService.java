package dynamicUi.demo.service;

import dynamicUi.demo.entity.WorkflowStep;
import dynamicUi.demo.entity.WorkflowStepType;
import dynamicUi.demo.repoistory.WorkflowConfigurationRepository;
import dynamicUi.demo.repoistory.WorkflowStepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkflowStepService {

    private final WorkflowStepRepository workflowStepRepository;
    private final WorkflowConfigurationRepository workflowConfigurationRepository;

    public List<WorkflowStep> findAll() {
        return workflowStepRepository.findAll();
    }

    public WorkflowStep create(WorkflowStep step) {
        if (step.getCode() == null || step.getCode().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Step code is required.");
        }
        if (step.getName() == null || step.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Step name is required.");
        }

        String normalizedCode = step.getCode().trim().toUpperCase();
        validateKnownStepType(normalizedCode);

        if (workflowStepRepository.existsByCode(normalizedCode)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A workflow step with code '" + normalizedCode + "' already exists.");
        }

        step.setCode(normalizedCode);
        step.setName(step.getName().trim());

        return workflowStepRepository.save(step);
    }

    public WorkflowStep update(Long id, WorkflowStep payload) {
        WorkflowStep existing = workflowStepRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workflow step not found: " + id));

        if (payload.getName() == null || payload.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Step name is required.");
        }

        // code is intentionally immutable — it's what binds this row to the
        // real business logic in GateCheckInService/TruckInspectionService/etc.
        existing.setName(payload.getName().trim());
        existing.setDescription(payload.getDescription());

        return workflowStepRepository.save(existing);
    }

    public void delete(Long id) {
        if (!workflowStepRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Workflow step not found: " + id);
        }
        if (workflowConfigurationRepository.existsByWorkflowStep_Id(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This step is used in the workflow configuration. Remove it from the configuration first."
            );
        }
        workflowStepRepository.deleteById(id);
    }

    /**
     * Step behaviour is coded per WorkflowStepType (see GateCheckInService,
     * TruckInspectionService, ...), so we don't let admins invent arbitrary
     * new codes that have no corresponding implementation.
     */
    private void validateKnownStepType(String code) {
        try {
            WorkflowStepType.valueOf(code);
        } catch (IllegalArgumentException ex) {
            String allowed = Arrays.stream(WorkflowStepType.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unknown step code '" + code + "'. Must be one of: " + allowed
            );
        }
    }
}
