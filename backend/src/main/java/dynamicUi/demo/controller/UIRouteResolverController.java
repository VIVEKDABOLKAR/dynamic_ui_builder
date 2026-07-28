package dynamicUi.demo.controller;

import dynamicUi.demo.dto.NavigationNodeDTO;
import dynamicUi.demo.dto.RouteResponseDTO;
import dynamicUi.demo.service.NavigationBuilderService;
import dynamicUi.demo.service.inter.UIRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Frontend-facing route resolution + navigation.
 * Sits under /api/ui/** (same auth bucket as /api/ui/pages) — unlike
 * UIRouteController, this is not admin CRUD, it's what the router and
 * sidebar call at runtime.
 */
@RestController
@RequestMapping("/api/ui/routes")
@RequiredArgsConstructor
public class UIRouteResolverController {

    private final UIRouteService uiRouteService;
    private final NavigationBuilderService navigationBuilderService;


    /**
     * Phase 1 — GET /api/ui/routes/resolve?path=/gate-checkin
     *
     * Browser URL → Match Route Path → Fetch UIRoute → Get PageCode.
     * Frontend then calls GET /api/ui/pages/{pageCode} with the pageCode
     * from the response to fetch the assembled page JSON and render it.
     */
    @GetMapping("/resolve")
    public ResponseEntity<RouteResponseDTO> resolve(@RequestParam String path) {
        return ResponseEntity.ok(uiRouteService.resolveByPath(path));
    }

    /**
     * Phase 2 — GET /api/ui/routes/navigation
     *
     * UIRoute Table → Navigation Builder → Sidebar.
     * Returns the sidebar tree already grouped (by parentMenu) and ordered
     * (by menuOrder) — the frontend just renders it.
     */
    @GetMapping("/navigation")
    public ResponseEntity<List<NavigationNodeDTO>> navigation() {
        return ResponseEntity.ok(navigationBuilderService.buildSidebar());
    }
}
