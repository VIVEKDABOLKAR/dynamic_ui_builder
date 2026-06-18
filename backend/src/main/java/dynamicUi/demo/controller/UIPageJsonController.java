package dynamicUi.demo.controller;

import dynamicUi.demo.dto.PageJsonDTO;
import dynamicUi.demo.entity.UIPage;
import dynamicUi.demo.entity.UIPageJson;
import dynamicUi.demo.service.PageAssemblerService;
import dynamicUi.demo.service.inter.UIPageJsonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// FIX: Removed @CrossOrigin — CORS is now configured globally in SecurityConfig.
//      Having both @CrossOrigin("*") here and a restrictive CorsConfigurationSource
//      in SecurityConfig produced conflicting headers and caused 403 / preflight failures.
@RestController
@RequestMapping("/api/ui/pages")
public class UIPageJsonController {

    private final UIPageJsonService    uiPageJsonService;
    private final PageAssemblerService pageAssemblerService;

    public UIPageJsonController(UIPageJsonService uiPageJsonService,
                                PageAssemblerService pageAssemblerService) {
        this.uiPageJsonService    = uiPageJsonService;
        this.pageAssemblerService = pageAssemblerService;
    }

    /**
     * GET /api/ui/pages/{pageCode}
     *
     * Returns the fully assembled page JSON — components + actions merged from DB.
     * This is the endpoint the frontend renderer (DynamicPage) should call.
     */
    @GetMapping("/{pageCode}")
    public ResponseEntity<PageJsonDTO> getAssembledPage(@PathVariable String pageCode) {
        return ResponseEntity.ok(pageAssemblerService.getPageAssembledByPageCode(pageCode));
    }

    /**
     * GET /api/ui/pages/{pageCode}/raw
     *
     * Returns the raw stored json_schema without assembly.
     * Used by the admin JSON editor.
     */
    @GetMapping("/{pageCode}/raw")
    public ResponseEntity<UIPageJson> getPageJsonRaw(@PathVariable String pageCode) {
        return ResponseEntity.ok(uiPageJsonService.getByPageCode(pageCode));
    }

    /**
     * GET /api/ui/pages
     *
     * Returns all active pages (for navigation / sidebar).
     */
    @GetMapping
    public ResponseEntity<List<UIPage>> getAllActivePages() {
        return ResponseEntity.ok(uiPageJsonService.getAllActivePages());
    }
}