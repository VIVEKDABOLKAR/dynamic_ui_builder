package dynamicUi.demo.service;

import dynamicUi.demo.dto.RouteAccessDTO;
import dynamicUi.demo.dto.RouteAccessResponse;
import dynamicUi.demo.entity.Facility;
import dynamicUi.demo.entity.FacilityRouteAccess;
import dynamicUi.demo.entity.UIRoute;
import dynamicUi.demo.repoistory.FacilityRepository;
import dynamicUi.demo.repoistory.FacilityRouteAccessRepository;
import dynamicUi.demo.repoistory.UIRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityRouteAccessService {

    private final FacilityRouteAccessRepository accessRepository;
    private final UIRouteRepository uiRouteRepository;
    private final FacilityRepository facilityRepository;

    //---------------------------
    // Reads
    //---------------------------

    public RouteAccessResponse getAccessForFacility(String facilityId) {
        if (!facilityRepository.existsById(facilityId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Facility not found: " + facilityId);
        }
        List<FacilityRouteAccess> byFacilityIdAndActiveTrue = accessRepository.findByFacilityIdAndActiveTrue(facilityId);
        Set<Long> granted = byFacilityIdAndActiveTrue
                .stream()
                .map(FacilityRouteAccess::getRouteId)
                .collect(Collectors.toSet());

        return RouteAccessResponse.builder()
                .facilityId(facilityId)
                .global(false)
                .routes(toDTOs(granted))
                .build();
    }

    /**
     * "Global" has no row of its own — this shows the intersection of what
     * every facility currently has granted, i.e. the routes that are
     * common to all facilities right now.
     */
    public RouteAccessResponse getGlobalAccess() {
        List<Facility> facilities = facilityRepository.findAll();

        Set<Long> intersection;
        if (facilities.isEmpty()) {
            intersection = Set.of();
        } else {
            intersection = null;
            for (Facility f : facilities) {
                Set<Long> granted = accessRepository.findByFacilityIdAndActiveTrue(f.getId())
                        .stream()
                        .map(FacilityRouteAccess::getRouteId)
                        .collect(Collectors.toSet());

                if (intersection == null) {
                    intersection = new HashSet<>(granted);
                } else {
                    intersection.retainAll(granted);
                }
            }
        }

        return RouteAccessResponse.builder()
                .facilityId(null)
                .global(true)
                .routes(toDTOs(intersection))
                .build();
    }

    private List<RouteAccessDTO> toDTOs(Set<Long> grantedRouteIds) {
        return uiRouteRepository.findByIsActiveTrueOrderByMenuOrderAsc()
                .stream()
                .map(route -> RouteAccessDTO.builder()
                        .routeId(route.getId())
                        .routeCode(route.getRouteCode())
                        .pageName(route.getPage() != null ? route.getPage().getPageName() : null)
                        .path(route.getPath())
                        .granted(grantedRouteIds.contains(route.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    //---------------------------
    // Writes
    //---------------------------

    @Transactional
    public void updateFacilityAccess(String facilityId, List<Long> selectedRouteIds) {
        if (!facilityRepository.existsById(facilityId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Facility not found: " + facilityId);
        }
        applyAccess(facilityId, selectedRouteIds);
    }

    /**
     * Bulk-writes the given route set to every facility. No "Global" row
     * is ever stored — this just loops over real facilities.
     */
    @Transactional
    public void updateGlobalAccess(List<Long> selectedRouteIds) {
        List<Facility> facilities = facilityRepository.findAll();
        for (Facility facility : facilities) {
            applyAccess(facility.getId(), selectedRouteIds);
        }
    }

    private void applyAccess(String facilityId, List<Long> selectedRouteIds) {
        Set<Long> selected = selectedRouteIds == null ? Set.of() : new HashSet<>(selectedRouteIds);

        for (Long routeId : selected) {
            if (!uiRouteRepository.existsById(routeId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown route id: " + routeId);
            }
        }

        List<FacilityRouteAccess> existingRows = accessRepository.findByFacilityId(facilityId);
        Map<Long, FacilityRouteAccess> byRoute = existingRows.stream()
                .collect(Collectors.toMap(FacilityRouteAccess::getRouteId, r -> r));

        // Grant: create or re-activate every selected route
        for (Long routeId : selected) {
            FacilityRouteAccess row = byRoute.get(routeId);
            if (row == null) {
                accessRepository.save(FacilityRouteAccess.builder()
                        .facilityId(facilityId)
                        .routeId(routeId)
                        .active(true)
                        .build());
            } else if (!row.isActive()) {
                row.setActive(true);
                accessRepository.save(row);
            }
        }

        // Revoke: deactivate anything currently active that wasn't selected
        for (FacilityRouteAccess row : existingRows) {
            if (row.isActive() && !selected.contains(row.getRouteId())) {
                row.setActive(false);
                accessRepository.save(row);
            }
        }
    }
}
