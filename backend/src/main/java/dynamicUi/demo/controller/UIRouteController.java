package dynamicUi.demo.controller;

import dynamicUi.demo.dto.RouteResponseDTO;
import dynamicUi.demo.entity.UIRoute;
import dynamicUi.demo.service.inter.UIRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/routes")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UIRouteController {

    private final UIRouteService uiRouteService;

    /**
     * Phase 1 — GET /api/admin/routes/resolve?path=/gate-checkin
     *
     * Browser URL → Match Route Path → Fetch UIRoute → Get PageCode.
     * Frontend then calls GET /api/ui/pages/{pageCode} with the pageCode
     * from the response to fetch the assembled page JSON and render it.
     */
    @GetMapping("/resolve")
    public ResponseEntity<RouteResponseDTO> resolve(@RequestParam String path) {
        return ResponseEntity.ok(uiRouteService.resolveByPath(path));
    }

    @PostMapping
    public UIRoute create(@RequestBody UIRoute route) {
        return uiRouteService.createRoute(route);
    }

    @GetMapping
    public List<UIRoute> getAll() {
        return uiRouteService.getAllRoutes();
    }

    @GetMapping("/{routeCode}")
    public UIRoute get(@PathVariable String routeCode) {
        return uiRouteService.getRoute(routeCode);
    }

    @PutMapping("/{routeCode}")
    public UIRoute update(
            @PathVariable String routeCode,
            @RequestBody UIRoute route) {

        return uiRouteService.updateRoute(routeCode, route);
    }

    @DeleteMapping("/{routeCode}")
    public void delete(@PathVariable String routeCode) {

        uiRouteService.deleteRoute(routeCode);
    }
}