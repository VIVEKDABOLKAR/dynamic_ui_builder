package dynamicUi.demo.controller;

import dynamicUi.demo.dto.RouteAccessResponse;
import dynamicUi.demo.dto.RouteAccessUpdateRequest;
import dynamicUi.demo.service.FacilityRouteAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Admin-only. Sits under /api/admin/** which SecurityConfig already
 * restricts to ROLE_ADMIN.
 *
 * The route master list itself is served by the existing
 * GET /api/admin/routes (UIRouteController) — reused as-is rather than
 * duplicated into a separate RouteMaster table.
 */
@RestController
@RequiredArgsConstructor
public class FacilityRouteAccessAdminController {

    private final FacilityRouteAccessService routeAccessService;

    @GetMapping("/api/admin/facilities/{facilityId}/route-access")
    public RouteAccessResponse getFacilityAccess(@PathVariable String facilityId) {
        return routeAccessService.getAccessForFacility(facilityId);
    }

    @PutMapping("/api/admin/facilities/{facilityId}/route-access")
    public RouteAccessResponse updateFacilityAccess(
            @PathVariable String facilityId,
            @RequestBody RouteAccessUpdateRequest request
    ) {
        routeAccessService.updateFacilityAccess(facilityId, request.getRouteIds());
        return routeAccessService.getAccessForFacility(facilityId);
    }

    /**
     * "Global" isn't a stored facility — this shows the intersection of
     * what every real facility currently has granted.
     */
    @GetMapping("/api/admin/route-access/global")
    public RouteAccessResponse getGlobalAccess() {
        return routeAccessService.getGlobalAccess();
    }

    /**
     * Bulk-writes the given route set to every facility. Does not create
     * any "Global" row.
     */
    @PutMapping("/api/admin/route-access/global")
    public RouteAccessResponse updateGlobalAccess(@RequestBody RouteAccessUpdateRequest request) {
        routeAccessService.updateGlobalAccess(request.getRouteIds());
        return routeAccessService.getGlobalAccess();
    }
}
