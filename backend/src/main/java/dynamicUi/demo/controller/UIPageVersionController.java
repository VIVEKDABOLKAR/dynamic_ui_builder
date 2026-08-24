package dynamicUi.demo.controller;

import dynamicUi.demo.dto.CreatePageVersionRequest;
import dynamicUi.demo.dto.PageVersionDTO;
import dynamicUi.demo.service.inter.UIPageVersionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/pages/{pageCode}/versions")
public class UIPageVersionController {

    private final UIPageVersionService uiPageVersionService;

    public UIPageVersionController(UIPageVersionService uiPageVersionService) {
        this.uiPageVersionService = uiPageVersionService;
    }

    /** GET /api/admin/pages/{pageCode}/versions — history, newest first. */
    @GetMapping
    public ResponseEntity<List<PageVersionDTO>> getHistory(@PathVariable String pageCode) {
        return ResponseEntity.ok(uiPageVersionService.getVersionHistory(pageCode));
    }

    /** POST /api/admin/pages/{pageCode}/versions — snapshot current JSON as a new version. */
    @PostMapping
    public ResponseEntity<PageVersionDTO> createVersion(@PathVariable String pageCode,
                                                          @RequestBody(required = false) CreatePageVersionRequest request) {
        String changeSummary = request != null ? request.getChangeSummary() : null;
        return ResponseEntity.ok(uiPageVersionService.createVersion(pageCode, changeSummary));
    }

    /** GET /api/admin/pages/{pageCode}/versions/{versionNumber} — full snapshot, for preview. */
    @GetMapping("/{versionNumber}")
    public ResponseEntity<PageVersionDTO> getVersion(@PathVariable String pageCode,
                                                       @PathVariable Integer versionNumber) {
        return ResponseEntity.ok(uiPageVersionService.getVersion(pageCode, versionNumber));
    }

    /** POST /api/admin/pages/{pageCode}/versions/{versionNumber}/restore — load into current working state. */
    @PostMapping("/{versionNumber}/restore")
    public ResponseEntity<Void> restoreVersion(@PathVariable String pageCode,
                                                @PathVariable Integer versionNumber) {
        uiPageVersionService.restoreVersion(pageCode, versionNumber);
        return ResponseEntity.ok().build();
    }
}
