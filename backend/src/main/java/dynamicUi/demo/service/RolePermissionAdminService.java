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
 * Note: creating a role here only adds a row an admin can attach
 * permission_pattern rows to. It does NOT make it assignable to a user
 * on its own — that still requires adding the matching value to the
 * security.Role enum, since AppUser.role is a typed enum column. See
 * AppRole's class comment.
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

    @Transactional
    public RoleAccessResponse createRole(String rawCode, String name, String description) {
        String code = normalizeCode(rawCode);

        if (appRoleRepository.existsByCode(code)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Role already exists: " + code);
        }

        AppRole role = appRoleRepository.save(
                AppRole.builder()
                        .code(code)
                        .name(name != null && !name.isBlank() ? name.trim() : code)
                        .description(description != null ? description.trim() : null)
                        .build()
        );

        return toResponse(role);
    }

    private AppRole findRoleOrThrow(String code) {
        return appRoleRepository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Role not found: " + code));
    }

    /**
     * ROLE_ADMIN / ROLE_VIEWER convention — uppercases, replaces
     * spaces/dashes with underscores, and prefixes "ROLE_" if missing,
     * so a code entered as "gate operator" becomes "ROLE_GATE_OPERATOR".
     */
    private String normalizeCode(String rawCode) {
        if (rawCode == null || rawCode.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role code is required");
        }

        String normalized = rawCode.trim().toUpperCase().replaceAll("[\\s-]+", "_");
        if (!normalized.startsWith("ROLE_")) {
            normalized = "ROLE_" + normalized;
        }
        return normalized;
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

