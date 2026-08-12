package dynamicUi.demo.controller;

import dynamicUi.demo.dto.RoleAccessResponse;
import dynamicUi.demo.dto.RolePermissionUpdateRequest;
import dynamicUi.demo.service.RolePermissionAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin-only. Sits under /api/admin/** which SecurityConfig authenticates.
 *
 * Lets an admin view/edit a role's permission patterns (e.g. "*",
 * "gate.*") without a redeploy. See RolePermissionAdminService for the
 * scope note on adding brand-new role codes.
 */
@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
public class RoleAdminController {

    private final RolePermissionAdminService rolePermissionAdminService;

    @GetMapping
    public List<RoleAccessResponse> getAllRoles() {
        return rolePermissionAdminService.getAllRoles();
    }

    @GetMapping("/{code}")
    public RoleAccessResponse getRole(@PathVariable String code) {
        return rolePermissionAdminService.getRole(code);
    }

    @PutMapping("/{code}/permissions")
    public RoleAccessResponse updatePermissions(
            @PathVariable String code,
            @RequestBody RolePermissionUpdateRequest request
    ) {
        return rolePermissionAdminService.updatePatterns(code, request.getPatterns());
    }
}
