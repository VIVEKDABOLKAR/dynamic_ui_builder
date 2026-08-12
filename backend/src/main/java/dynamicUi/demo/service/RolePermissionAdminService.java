package dynamicUi.demo.service;

import dynamicUi.demo.dto.RoleAccessResponse;
import dynamicUi.demo.entity.AppRole;
import dynamicUi.demo.entity.RolePermission;
import dynamicUi.demo.repoistory.AppRoleRepository;
import dynamicUi.demo.repoistory.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Admin-only. Sits under /api/admin/** (SecurityConfig already restricts
 * this bucket to authenticated users; tighten to hasRole("ADMIN") there
 * if/when that's enforced consistently across the other /api/admin/**
 * controllers).
 *
 * Note: this only manages permission_pattern rows for EXISTING role
 * codes (seeded: ROLE_ADMIN, ROLE_VIEWER). Adding a brand-new role that
 * a user can actually be assigned requires also adding it to the
 * security.Role enum — see AppRole's class comment.
 */
@Service
@RequiredArgsConstructor
public class RolePermissionAdminService {

    private final AppRoleRepository appRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Transactional(readOnly = true)
    public List<RoleAccessResponse> getAllRoles() {
        return appRoleRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoleAccessResponse getRole(String code) {
        return toResponse(findRoleOrThrow(code));
    }

    @Transactional
    public RoleAccessResponse updatePatterns(String code, List<String> patterns) {
        AppRole role = findRoleOrThrow(code);

        rolePermissionRepository.deleteByAppRole_Id(role.getId());

        if (patterns != null) {
            List<RolePermission> toSave = patterns.stream()
                    .filter(p -> p != null && !p.isBlank())
                    .map(p -> RolePermission.builder()
                            .appRole(role)
                            .permissionPattern(p.trim())
                            .build())
                    .toList();
            rolePermissionRepository.saveAll(toSave);
        }

        return toResponse(role);
    }

    private AppRole findRoleOrThrow(String code) {
        return appRoleRepository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Role not found: " + code));
    }

    private RoleAccessResponse toResponse(AppRole role) {
        List<String> patterns = rolePermissionRepository.findByAppRole_Code(role.getCode()).stream()
                .map(RolePermission::getPermissionPattern)
                .toList();

        return RoleAccessResponse.builder()
                .code(role.getCode())
                .name(role.getName())
                .patterns(patterns)
                .build();
    }
}
