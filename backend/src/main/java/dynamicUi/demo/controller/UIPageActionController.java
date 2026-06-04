package dynamicUi.demo.controller;


import dynamicUi.demo.dto.ComponentActionRequest;
import dynamicUi.demo.entity.UIPageAction;
import dynamicUi.demo.service.inter.UIPageActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/actions")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UIPageActionController {

    private final UIPageActionService service;

    @PostMapping("/component/{pageCode}")
    public ResponseEntity<?> addComponentAction(
            @PathVariable String pageCode,
            @RequestBody ComponentActionRequest request) {

        service.addComponentAction(pageCode, request);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{pageCode}")
    public UIPageAction create(
            @RequestBody UIPageAction uiPageAction,
            @PathVariable String pageCode) {
        uiPageAction.setUiPagecode(pageCode);
        return service.create(pageCode, uiPageAction);
    }

    @PutMapping("/{id}")
    public UIPageAction update(
            @PathVariable Long id,
            @RequestBody UIPageAction uiPageAction) {
        return service.update(id, uiPageAction);
    }

    @GetMapping("/{id}")
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
