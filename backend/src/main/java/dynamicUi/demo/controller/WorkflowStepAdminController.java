package dynamicUi.demo.controller;

import dynamicUi.demo.entity.WorkflowStep;
import dynamicUi.demo.service.WorkflowStepService;
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
@RequestMapping("/api/admin/workflow-steps")
@RequiredArgsConstructor
public class WorkflowStepAdminController {

    private final WorkflowStepService workflowStepService;

    @GetMapping
    public List<WorkflowStep> getAll() {
        return workflowStepService.findAll();
    }

    @PostMapping
    public ResponseEntity<WorkflowStep> create(@RequestBody WorkflowStep step) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workflowStepService.create(step));
    }

    @PutMapping("/{id}")
    public WorkflowStep update(@PathVariable Long id, @RequestBody WorkflowStep step) {
        return workflowStepService.update(id, step);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workflowStepService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
