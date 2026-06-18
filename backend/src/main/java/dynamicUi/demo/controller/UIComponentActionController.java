package dynamicUi.demo.controller;

import dynamicUi.demo.entity.UIComponentAction;
import dynamicUi.demo.service.inter.UIComponentActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/component-actions")
@RequiredArgsConstructor
public class UIComponentActionController {

    private final UIComponentActionService service;

    /**
     * POST /api/admin/component-actions
     * Create a new component action.
     *
     * Request body:
     * {
     *   "componentId": 25,
     *   "pageCode":    "home",
     *   "event":       "onClick",
     *   "actionRef":   "saveHome",
     *   "conditionExpr": "true",
     *   "sequenceNo":  1
     * }
     */
    @PostMapping
    public ResponseEntity<UIComponentAction> create(@RequestBody UIComponentAction action) {
        return ResponseEntity.ok(service.create(action));
    }

    /**
     * PUT /api/admin/component-actions/{id}
     * Update an existing component action.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UIComponentAction> update(
            @PathVariable Long id,
            @RequestBody UIComponentAction action) {
        return ResponseEntity.ok(service.update(id, action));
    }

    /**
     * GET /api/admin/component-actions/{id}
     * Get a single component action by its id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UIComponentAction> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    /**
     * GET /api/admin/component-actions/component/{componentId}
     * Get all actions for a specific component.
     */
    @GetMapping("/component/{componentId}")
    public ResponseEntity<List<UIComponentAction>> getByComponentId(
            @PathVariable Long componentId) {
        return ResponseEntity.ok(service.getByComponentId(componentId));
    }

    /**
     * GET /api/admin/component-actions/page/{pageCode}
     * Get all component actions for a page (useful for admin editor overview).
     */
    @GetMapping("/page/{pageCode}")
    public ResponseEntity<List<UIComponentAction>> getByPageCode(
            @PathVariable String pageCode) {
        return ResponseEntity.ok(service.getByPageCode(pageCode));
    }

    /**
     * DELETE /api/admin/component-actions/{id}
     * Delete a single component action.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /api/admin/component-actions/component/{componentId}
     * Delete ALL actions for a component (used when the component itself is deleted).
     */
    @DeleteMapping("/component/{componentId}")
    public ResponseEntity<Void> deleteByComponentId(@PathVariable Long componentId) {
        service.deleteByComponentId(componentId);
        return ResponseEntity.noContent().build();
    }
}