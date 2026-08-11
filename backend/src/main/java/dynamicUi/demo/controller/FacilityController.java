package dynamicUi.demo.controller;

import dynamicUi.demo.constant.Attribute;
import dynamicUi.demo.dto.FacilityAccessRequest;
import dynamicUi.demo.dto.JwtResponseDTO;
import dynamicUi.demo.entity.Facility;
import dynamicUi.demo.entity.UserFacilityAccess;
import dynamicUi.demo.repoistory.UserFacilityAccessRepository;
import dynamicUi.demo.service.FacilityService;
import dynamicUi.demo.service.UserFacilityAccessService;
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
    private final UserFacilityAccessService userFacilityAccessService;

    @GetMapping
    public List<Facility> getAllFacilities() {
        return facilityService.findAll();
    }

    @PostMapping("/request")
    public ResponseEntity<Void> createFacilityAccessRequest(
            @RequestAttribute(value = Attribute.CURRENT_USERNAME, required = true) String username,
            @RequestBody FacilityAccessRequest facilityAccessRequest
    ) {
        UserFacilityAccess userFacilityAccess =
                userFacilityAccessService.createAccessRequest(
                        username,
                        facilityAccessRequest.getFacilityId()
                );


        return ResponseEntity.status(HttpStatus.CREATED).build();
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