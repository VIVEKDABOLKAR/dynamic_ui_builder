package dynamicUi.demo.controller;

import dynamicUi.demo.constant.Attribute;
import dynamicUi.demo.dto.FacilityAccessRequest;
import dynamicUi.demo.entity.AccessStatus;
import dynamicUi.demo.entity.UserFacilityAccess;
import dynamicUi.demo.repoistory.UserFacilityAccessRepository;
import dynamicUi.demo.service.UserFacilityAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/facility-access")
@RequiredArgsConstructor
public class FacilityAccessAdminController {

    private final UserFacilityAccessRepository accessRepo;
    private final UserFacilityAccessService userFacilityAccessService;

    @GetMapping("/pending")
    @PreAuthorize("has role admin")
    public List<UserFacilityAccess> getPending() {
        return accessRepo.findByStatus(AccessStatus.PENDING);
    }



    @PostMapping("/{id}/approve")
    @PreAuthorize("has role admin")
    public void approve(@PathVariable Long id) {
        UserFacilityAccess access = accessRepo.findById(id).orElseThrow();
        access.setStatus(AccessStatus.APPROVED);
        accessRepo.save(access);
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("has role admin")
    public void reject(@PathVariable Long id) {
        UserFacilityAccess access = accessRepo.findById(id).orElseThrow();
        access.setStatus(AccessStatus.REJECTED);
        accessRepo.save(access);
    }
}