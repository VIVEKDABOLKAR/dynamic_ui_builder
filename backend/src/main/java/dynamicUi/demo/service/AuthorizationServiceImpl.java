package dynamicUi.demo.service;

import dynamicUi.demo.entity.UIPage;
import dynamicUi.demo.repoistory.RolePermissionRepository;
import dynamicUi.demo.service.inter.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {

    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public boolean hasPermission(String permission) {
        if (permission == null || permission.isBlank()) {
            return true; // nothing to enforce
        }

        String roleCode = currentRoleCode();
        if (roleCode == null) {
            // Should not normally be reachable — /api/ui/** already requires
            // authentication upstream — but never fail open on this.
            return false;
        }

        List<String> patterns = rolePermissionRepository.findPatternsByRoleCode(roleCode);

        if (patterns.isEmpty()) {
            // Fail-open: this role has no permission_pattern rows configured
            // yet. Mirrors FacilityRouteAccess's "zero rows = not configured
            // = allow all" behavior so nothing breaks on rollout.
            return true;
        }

        return patterns.stream().anyMatch(pattern -> PermissionPatternMatcher.matches(pattern, permission));
    }

    @Override
    public void requirePermission(String permission) {
        if (!hasPermission(permission)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not have permission to access: " + permission
            );
        }
    }

    @Override
    public String resolvePagePermission(UIPage page) {
        if (page == null) {
            return null;
        }

        if (page.getPermissionCode() != null && !page.getPermissionCode().isBlank()) {
            return page.getPermissionCode();
        }

        return derivePermissionFromPageCode(page.getPageCode());
    }

    @Override
    public void requirePagePermission(UIPage page) {
        String permission = resolvePagePermission(page);
        if (permission == null || permission.isBlank()) {
            return; // no permission to enforce
        }
        requirePermission(permission);
    }

    // ── helpers ──────────────────────────────────────────────────────────

    /**
     * GATE_CHECKIN        -> gate.checkin
     * PARKING_ALLOCATION  -> parking.allocation
     * DASHBOARD           -> dashboard
     *
     * Naming convention from the proposal: <module>.<page>
     */
    private String derivePermissionFromPageCode(String pageCode) {
        if (pageCode == null || pageCode.isBlank()) {
            return null;
        }

        String[] parts = pageCode.toLowerCase().split("_");
        if (parts.length == 1) {
            return parts[0];
        }

        String module = parts[0];
        String rest = String.join(".", Arrays.copyOfRange(parts, 1, parts.length));
        return module + "." + rest;
    }

    private String currentRoleCode() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(null);
    }
}
