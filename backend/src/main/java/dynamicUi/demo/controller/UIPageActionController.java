package dynamicUi.demo.controller;

import dynamicUi.demo.dto.ComponentActionRequest;
import dynamicUi.demo.entity.UIPageAction;
import dynamicUi.demo.service.inter.UIPageActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// FIX: Removed @CrossOrigin — CORS handled globally in SecurityConfig.
@RestController
@RequestMapping("/api/admin/actions")
@RequiredArgsConstructor
public class UIPageActionController {

    private final UIPageActionService service;

    // ── Component-level action ────────────────────────────────────────────────

    @PostMapping("/component/{pageCode}")
    public ResponseEntity<?> addComponentAction(
            @PathVariable String pageCode,
            @RequestBody ComponentActionRequest request) {
        service.addComponentAction(pageCode, request);
        return ResponseEntity.ok().build();
    }

    // ── Page-level actions ────────────────────────────────────────────────────

    // FIX: was @PostMapping("/{pageCode}") — same base path as @GetMapping("/{id}")
    // caused Spring to ambiguously match GET /actions/123 to both routes.
    // Changed to /page/{pageCode} for page-scoped endpoints.
    @PostMapping("/page/{pageCode}")
    public UIPageAction create(
            @PathVariable String pageCode,
            @RequestBody UIPageAction uiPageAction) {
        uiPageAction.setUiPagecode(pageCode);
        return service.create(pageCode, uiPageAction);
    }

    @PutMapping("/{id}")
    public UIPageAction update(
            @PathVariable Long id,
            @RequestBody UIPageAction uiPageAction) {
        return service.update(id, uiPageAction);
    }

    // FIX: was @GetMapping("/{id}") — same pattern as page-scoped GET.
    // Changed to /id/{id} so it is unambiguous.
    @GetMapping("/id/{id}")
    public UIPageAction getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/page/{pageCode}")
    public List<UIPageAction> getByPageCode(@PathVariable String pageCode) {
        return service.getByPageCode(pageCode);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}