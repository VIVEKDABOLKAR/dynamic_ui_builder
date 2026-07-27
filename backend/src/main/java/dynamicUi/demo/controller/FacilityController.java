package dynamicUi.demo.controller;

import dynamicUi.demo.entity.Facility;
import dynamicUi.demo.service.FacilityService;
import lombok.RequiredArgsConstructor;
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
}