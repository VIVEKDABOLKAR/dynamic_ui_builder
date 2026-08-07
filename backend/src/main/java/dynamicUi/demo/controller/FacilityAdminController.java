package dynamicUi.demo.controller;

import dynamicUi.demo.entity.Facility;
import dynamicUi.demo.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin-only facility CRUD. Sits under /api/admin/** which is already
 * restricted to ROLE_ADMIN in SecurityConfig — unlike /api/facilities/**,
 * which is intentionally permitAll for read access by any logged-in user.
 */
@RestController
@RequestMapping("/api/admin/facilities")
@RequiredArgsConstructor
public class FacilityAdminController {

    private final FacilityService facilityService;

    @PostMapping
    public ResponseEntity<Facility> createFacility(@RequestBody Facility facility) {
        Facility created = facilityService.createFacility(facility);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public Facility updateFacility(@PathVariable String id, @RequestBody Facility facility) {
        return facilityService.updateFacility(id, facility);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFacility(@PathVariable String id) {
        facilityService.deleteFacility(id);
        return ResponseEntity.noContent().build();
    }
}
