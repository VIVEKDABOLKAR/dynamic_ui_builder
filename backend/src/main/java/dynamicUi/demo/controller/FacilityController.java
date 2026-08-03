package dynamicUi.demo.controller;

import dynamicUi.demo.dto.JwtResponseDTO;
import dynamicUi.demo.entity.Facility;
import dynamicUi.demo.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facilities")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    @GetMapping
    public List<Facility> getAllFacilities() {
        return facilityService.findAll();
    }

    @GetMapping("/accessible")
    public List<Facility> getAccessibleFacilities(Authentication authentication) {
        return facilityService.findAccessibleFacilities(authentication.getName());
    }

    @PostMapping("/change-facility")
    public ResponseEntity<?> changeFacility(
            @RequestParam String facilityId,
            @RequestHeader("Authorization") String authHeader) {

        String oldToken = authHeader.replace("Bearer ", "");
        String token = facilityService.changeFacility(oldToken, facilityId);

        if(token == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("User does not have approved access to facility: " + facilityId);
        }
        return ResponseEntity.ok(new JwtResponseDTO(token));
    }
}