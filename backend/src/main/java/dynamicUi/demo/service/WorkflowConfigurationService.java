package dynamicUi.demo.service;

import dynamicUi.demo.constant.FacilityId;
import dynamicUi.demo.dto.WorkflowConfigurationDTO;
import dynamicUi.demo.dto.WorkflowConfigurationRequest;
import dynamicUi.demo.entity.WorkflowConfiguration;
import dynamicUi.demo.entity.WorkflowStep;
import dynamicUi.demo.entity.WorkflowStepType;
import dynamicUi.demo.repoistory.WorkflowConfigurationRepository;
import dynamicUi.demo.repoistory.WorkflowStepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkflowConfigurationService {

    private final WorkflowConfigurationRepository workflowConfigurationRepository;
    private final WorkflowStepRepository workflowStepRepository;

    //---------------------------
    // Admin CRUD
    //---------------------------

    public List<WorkflowConfigurationDTO> findAll() {
        return workflowConfigurationRepository.findAllByOrderBySequenceAsc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Raw rows for exactly this scope — no fallback. Used by the admin UI
     * to show/edit what's actually configured for the selected facility
     * (an empty list means "no override, currently falling back to global").
     */
    public List<WorkflowConfigurationDTO> findForFacilityEffictive(String facilityId) {
        //for global it is show everyone's facility_id
        if(facilityId.equals(FacilityId.GLOBAL.name())) {
            return findAll();
        }

        //if facility has no workflow config then return global workflow config
        List<WorkflowConfiguration> workflowConfigurationList = workflowConfigurationRepository.findByFacilityIdOrderBySequenceAsc(facilityId);

        if(workflowConfigurationList.isEmpty()){
            return findForFacilityEffictive(FacilityId.GLOBAL.name());
        }

        return workflowConfigurationList
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<WorkflowConfigurationDTO> findForFacility(String facilityId) {

        //if facility has no workflow config then return global workflow config
        List<WorkflowConfiguration> workflowConfigurationList = workflowConfigurationRepository.findByFacilityIdOrderBySequenceAsc(facilityId);
        return workflowConfigurationList
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public WorkflowConfigurationDTO create(WorkflowConfigurationRequest request, String facilityId) {
        WorkflowStep step = workflowStepRepository.findById(request.getWorkflowStepId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workflow step not found: " + request.getWorkflowStepId()));

        if (workflowConfigurationRepository.existsByWorkflowStep_IdAndFacilityId(step.getId(), facilityId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "'" + step.getName() + "' is already part of the workflow configuration.");
        }

        Integer sequence = request.getSequence();
        if (sequence == null) {
            // Scoped to this facility only — sequence restarts at 1 per
            // facility_id, it's not a global counter across every scope.
            sequence = workflowConfigurationRepository.findByFacilityIdOrderBySequenceAsc(facilityId).stream()
                    .mapToInt(WorkflowConfiguration::getSequence)
                    .max()
                    .orElse(0) + 1;
        }
        WorkflowConfiguration config = WorkflowConfiguration.builder()
                .workflowStep(step)
                .sequence(sequence)
                .active(request.isActive())
                .facilityId(facilityId)
                .build();

        WorkflowConfiguration saved = workflowConfigurationRepository.save(config);
        return toDTO(saved);
    }

    @Transactional
    public WorkflowConfigurationDTO update(Long id, WorkflowConfigurationRequest request) {
        WorkflowConfiguration existing = workflowConfigurationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workflow configuration not found: " + id));

        if (request.getSequence() != null) {
            existing.setSequence(request.getSequence());
        }

        // Guard against an admin disabling every step, which would silently
        // break Job Order creation the next time someone uses the app.
        if (!request.isActive() && existing.isActive() && activeCountExcluding(id) == 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "At least one workflow step must remain active.");
        }

        existing.setActive(request.isActive());

        return toDTO(workflowConfigurationRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        WorkflowConfiguration existing = workflowConfigurationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workflow configuration not found: " + id));

        if (existing.isActive() && activeCountExcluding(id) == 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "At least one workflow step must remain active. Deactivate a different step first, or add another before removing this one.");
        }

        workflowConfigurationRepository.deleteById(id);
    }

    private long activeCountExcluding(Long id) {
        return workflowConfigurationRepository.findByActiveTrueOrderBySequenceAsc().stream()
                .filter(c -> !c.getId().equals(id))
                .count();
    }

    //---------------------------
    // Runtime accessor — used by JobOrderService / JobStepService
    //---------------------------

    /**
     * The live, ordered workflow: active configuration rows, sorted by
     * sequence, translated to the WorkflowStepType each step's business
     * logic is actually keyed on.
     */
    public List<WorkflowStepType> getActiveStepsOrdered(String facilityId) {
        List<WorkflowStepType> steps = workflowConfigurationRepository.findByFacilityIdAndActiveTrueOrderBySequenceAsc(facilityId)
                .stream()
                .map(c -> {
                    try {
                        return WorkflowStepType.valueOf(c.getWorkflowStep().getCode());
                    } catch (IllegalArgumentException ex) {
                        throw new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Workflow configuration references unknown step code '" + c.getWorkflowStep().getCode() + "'."
                        );
                    }
                })
                .collect(Collectors.toList());

        if (steps.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No active workflow steps are configured. An admin needs to configure the workflow before Job Orders can be created.");
        }

        return steps;
    }

    public List<WorkflowStepType> getActiveEffectiveStepsOrdered(String facilityId) {
        List<WorkflowStepType> steps = workflowConfigurationRepository.findByFacilityIdAndActiveTrueOrderBySequenceAsc(facilityId)
                .stream()
                .map(c -> {
                    try {
                        return WorkflowStepType.valueOf(c.getWorkflowStep().getCode());
                    } catch (IllegalArgumentException ex) {
                        throw new ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "Workflow configuration references unknown step code '" + c.getWorkflowStep().getCode() + "'."
                        );
                    }
                })
                .collect(Collectors.toList());

        if (steps.isEmpty()) {
            steps = workflowConfigurationRepository.findByFacilityIdAndActiveTrueOrderBySequenceAsc(FacilityId.GLOBAL.name())
                    .stream()
                    .map(c -> {
                        try {
                            return WorkflowStepType.valueOf(c.getWorkflowStep().getCode());
                        } catch (IllegalArgumentException ex) {
                            throw new ResponseStatusException(
                                    HttpStatus.INTERNAL_SERVER_ERROR,
                                    "Workflow configuration references unknown step code '" + c.getWorkflowStep().getCode() + "'."
                            );
                        }
                    })
                    .collect(Collectors.toList());
        }

        return steps;
    }


    private WorkflowConfigurationDTO toDTO(WorkflowConfiguration c) {
        return WorkflowConfigurationDTO.builder()
                .id(c.getId())
                .workflowStepId(c.getWorkflowStep().getId())
                .workflowStepCode(c.getWorkflowStep().getCode())
                .workflowStepName(c.getWorkflowStep().getName())
                .facilityId(c.getFacilityId())
                .sequence(c.getSequence())
                .active(c.isActive())
                .build();
    }
}
