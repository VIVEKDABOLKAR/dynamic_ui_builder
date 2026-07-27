package dynamicUi.demo.controller;

import dynamicUi.demo.entity.AccessStatus;
import dynamicUi.demo.entity.UserFacilityAccess;
import dynamicUi.demo.repoistory.UserFacilityAccessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/facility-access")
@RequiredArgsConstructor
public class FacilityAccessAdminController {

    private final UserFacilityAccessRepository accessRepo;

    @GetMapping("/pending")
    public List<UserFacilityAccess> getPending() {
        return accessRepo.findByStatus(AccessStatus.PENDING);
    }

    @PostMapping("/{id}/approve")
    public void approve(@PathVariable Long id) {
        UserFacilityAccess access = accessRepo.findById(id).orElseThrow();
        access.setStatus(AccessStatus.APPROVED);
        accessRepo.save(access);
    }

    @PostMapping("/{id}/reject")
    public void reject(@PathVariable Long id) {
        UserFacilityAccess access = accessRepo.findById(id).orElseThrow();
        access.setStatus(AccessStatus.REJECTED);
        accessRepo.save(access);
    }
}