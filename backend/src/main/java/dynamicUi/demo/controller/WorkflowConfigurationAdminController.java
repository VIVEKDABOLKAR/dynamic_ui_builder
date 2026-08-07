package dynamicUi.demo.controller;

import dynamicUi.demo.constant.Attribute;
import dynamicUi.demo.constant.FacilityId;
import dynamicUi.demo.dto.WorkflowConfigurationDTO;
import dynamicUi.demo.dto.WorkflowConfigurationRequest;
import dynamicUi.demo.service.WorkflowConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin-only. Sits under /api/admin/** which SecurityConfig already
 * restricts to ROLE_ADMIN.
 */
@RestController
@RequestMapping("/api/admin/workflow-configurations")
@RequiredArgsConstructor
public class WorkflowConfigurationAdminController {

    private final WorkflowConfigurationService workflowConfigurationService;

    /**
     * If user is global facility then it will send every workflowConfiguration
     * Otherwise it will send facility based workflow
     * @return
     */
    @GetMapping
    public List<WorkflowConfigurationDTO> getEfeective(
            @RequestAttribute(value = Attribute.SELECTED_FACILITY_ID, required = true) String selectedFacilityId
    ) {
        return workflowConfigurationService.findForFacilityEffictive(selectedFacilityId);
    }

    @GetMapping("/list")
    public List<WorkflowConfigurationDTO> getList(
            @RequestAttribute(value = Attribute.SELECTED_FACILITY_ID, required = true) String selectedFacilityId
    ) {
        return workflowConfigurationService.findForFacility(selectedFacilityId);
    }

    @PostMapping
    public ResponseEntity<WorkflowConfigurationDTO> create(
            @RequestBody WorkflowConfigurationRequest request,
            @RequestAttribute(value = Attribute.SELECTED_FACILITY_ID, required = true) String selectedFacilityId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workflowConfigurationService.create(request, selectedFacilityId));
    }

    @PutMapping("/{id}")
    public WorkflowConfigurationDTO update(@PathVariable Long id, @RequestBody WorkflowConfigurationRequest request) {
        return workflowConfigurationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workflowConfigurationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
