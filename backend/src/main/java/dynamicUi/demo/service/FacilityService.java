package dynamicUi.demo.service;

import dynamicUi.demo.entity.Facility;
import dynamicUi.demo.repoistory.FacilityRepository;
import dynamicUi.demo.security.JwtUtil;
import dynamicUi.demo.security.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityService {

    private final FacilityRepository facilityRepository;
    private final JwtUtil jwtUtilService;

    public List<Facility> findAll() {
        return facilityRepository.findAll();
    }

    public List<Facility> findAccessibleFacilities(String username) {
        if(SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().contains(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name()))) {
            return facilityRepository.findAll();
        }
        return facilityRepository.findAccessibleFacilitiesByUsername(username);
    }

    public String changeFacility(String oldToken, String facilityId) {
        //validate facility allowed for user
        boolean facilityAllowed = existsAccessibleFacility(facilityId);

        if (facilityAllowed) {
            return jwtUtilService.generateToken(oldToken, facilityId);
        }

        //generator new token
        return null;
    }

    //---------------------------
    // Admin CRUD
    //---------------------------

    public Facility createFacility(Facility facility) {
        if (facility.getId() == null || facility.getId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Facility ID is required.");
        }
        if (facility.getName() == null || facility.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Facility name is required.");
        }

        String normalizedId = facility.getId().trim().toUpperCase();

        if (facilityRepository.existsById(normalizedId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A facility with ID '" + normalizedId + "' already exists.");
        }

        facility.setId(normalizedId);
        facility.setName(facility.getName().trim());

        return facilityRepository.save(facility);
    }

    public Facility updateFacility(String id, Facility payload) {
        Facility existing = facilityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Facility '" + id + "' not found."));

        if (payload.getName() == null || payload.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Facility name is required.");
        }

        existing.setName(payload.getName().trim());

        return facilityRepository.save(existing);
    }

    public void deleteFacility(String id) {
        if (!facilityRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Facility '" + id + "' not found.");
        }

        try {
            facilityRepository.deleteById(id);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Facility '" + id + "' cannot be deleted because it is referenced by other records (routes, access requests, etc)."
            );
        }
    }

    private boolean existsAccessibleFacility(String facilityId) {
        List<Facility> allowedFacilityList = findAccessibleFacilities(
                SecurityContextHolder.getContext().getAuthentication().getName()
        );
        Set<String> allowedFacilityIds = allowedFacilityList.stream()
                .map(Facility::getId)
                .collect(Collectors.toSet());

        return allowedFacilityIds.contains(facilityId);
    }
}