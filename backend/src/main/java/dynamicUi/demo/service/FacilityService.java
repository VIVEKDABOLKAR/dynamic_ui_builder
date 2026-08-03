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