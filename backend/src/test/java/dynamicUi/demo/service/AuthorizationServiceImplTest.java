package dynamicUi.demo.service;

import dynamicUi.demo.entity.UIPage;
import dynamicUi.demo.repoistory.RolePermissionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AuthorizationServiceImpl}.
 *
 * Focuses on the fail-open/fail-closed rules and page-permission
 * derivation described in the Security & Authorization Proposal.
 */
@ExtendWith(MockitoExtension.class)
class AuthorizationServiceImplTest {

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    @InjectMocks
    private AuthorizationServiceImpl authorizationService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAs(String roleAuthority) {
        var authentication = new UsernamePasswordAuthenticationToken(
                "test-user", "N/A", List.of(new SimpleGrantedAuthority(roleAuthority)));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // ── hasPermission ───────────────────────────────────────────────────

    @Test
    @DisplayName("blank/null permission is always allowed without hitting the repository")
    void blankPermissionAlwaysAllowed() {
        assertThat(authorizationService.hasPermission(null)).isTrue();
        assertThat(authorizationService.hasPermission("")).isTrue();
        assertThat(authorizationService.hasPermission("   ")).isTrue();
        verifyNoInteractions(rolePermissionRepository);
    }

    @Test
    @DisplayName("no authenticated user fails closed")
    void noAuthenticationFailsClosed() {
        SecurityContextHolder.clearContext();
        assertThat(authorizationService.hasPermission("gate.checkin")).isFalse();
        verifyNoInteractions(rolePermissionRepository);
    }

    @Test
    @DisplayName("role with zero permission rows fails open")
    void roleWithNoRowsFailsOpen() {
        authenticateAs("ROLE_GATE_OPERATOR");
        when(rolePermissionRepository.findPatternsByRoleCode("ROLE_GATE_OPERATOR"))
                .thenReturn(List.of());

        assertThat(authorizationService.hasPermission("gate.checkin")).isTrue();
    }

    @Test
    @DisplayName("role with a matching wildcard pattern is granted")
    void roleWithMatchingWildcardGranted() {
        authenticateAs("ROLE_GATE_OPERATOR");
        when(rolePermissionRepository.findPatternsByRoleCode("ROLE_GATE_OPERATOR"))
                .thenReturn(List.of("gate.*"));

        assertThat(authorizationService.hasPermission("gate.checkin")).isTrue();
    }

    @Test
    @DisplayName("role with only non-matching patterns is denied")
    void roleWithNonMatchingPatternsDenied() {
        authenticateAs("ROLE_GATE_OPERATOR");
        when(rolePermissionRepository.findPatternsByRoleCode("ROLE_GATE_OPERATOR"))
                .thenReturn(List.of("dock.*", "parking.view"));

        assertThat(authorizationService.hasPermission("gate.checkin")).isFalse();
    }

    @Test
    @DisplayName("ROLE_ADMIN with '*' pattern is granted any permission")
    void adminWithWildcardGrantedEverything() {
        authenticateAs("ROLE_ADMIN");
        when(rolePermissionRepository.findPatternsByRoleCode("ROLE_ADMIN"))
                .thenReturn(List.of("*"));

        assertThat(authorizationService.hasPermission("anything.at.all")).isTrue();
    }

    // ── requirePermission ───────────────────────────────────────────────

    @Test
    @DisplayName("requirePermission throws 403 when permission is denied")
    void requirePermissionThrowsForbiddenWhenDenied() {
        authenticateAs("ROLE_GATE_OPERATOR");
        when(rolePermissionRepository.findPatternsByRoleCode("ROLE_GATE_OPERATOR"))
                .thenReturn(List.of("dock.*"));

        assertThatThrownBy(() -> authorizationService.requirePermission("gate.checkin"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("gate.checkin");
    }

    @Test
    @DisplayName("requirePermission does not throw when permission is granted")
    void requirePermissionDoesNotThrowWhenGranted() {
        authenticateAs("ROLE_ADMIN");
        when(rolePermissionRepository.findPatternsByRoleCode("ROLE_ADMIN"))
                .thenReturn(List.of("*"));

        authorizationService.requirePermission("gate.checkin");
        // no exception == pass
    }

    // ── resolvePagePermission ───────────────────────────────────────────

    @Test
    @DisplayName("resolvePagePermission returns null for a null page")
    void resolvePagePermissionNullPage() {
        assertThat(authorizationService.resolvePagePermission(null)).isNull();
    }

    @Test
    @DisplayName("resolvePagePermission prefers an explicit permissionCode over derivation")
    void resolvePagePermissionPrefersExplicitCode() {
        UIPage page = UIPage.builder()
                .pageCode("GATE_CHECKIN")
                .permissionCode("custom.permission")
                .build();

        assertThat(authorizationService.resolvePagePermission(page)).isEqualTo("custom.permission");
    }

    @Test
    @DisplayName("resolvePagePermission derives module.page from page code when no explicit code set")
    void resolvePagePermissionDerivesFromPageCode() {
        UIPage page = UIPage.builder()
                .pageCode("GATE_CHECKIN")
                .permissionCode(null)
                .build();

        assertThat(authorizationService.resolvePagePermission(page)).isEqualTo("gate.checkin");
    }

    @Test
    @DisplayName("resolvePagePermission derives multi-segment permission for multi-part page code")
    void resolvePagePermissionDerivesMultiSegment() {
        UIPage page = UIPage.builder()
                .pageCode("PARKING_SPOT_ASSIGNMENT")
                .permissionCode("")
                .build();

        assertThat(authorizationService.resolvePagePermission(page)).isEqualTo("parking.spot.assignment");
    }

    @Test
    @DisplayName("resolvePagePermission returns single lowercase segment for a single-word page code")
    void resolvePagePermissionSingleSegment() {
        UIPage page = UIPage.builder()
                .pageCode("DASHBOARD")
                .permissionCode(null)
                .build();

        assertThat(authorizationService.resolvePagePermission(page)).isEqualTo("dashboard");
    }

    @Test
    @DisplayName("resolvePagePermission returns null when page code is blank")
    void resolvePagePermissionBlankPageCode() {
        UIPage page = UIPage.builder()
                .pageCode("")
                .permissionCode(null)
                .build();

        assertThat(authorizationService.resolvePagePermission(page)).isNull();
    }

    // ── requirePagePermission ───────────────────────────────────────────

    @Test
    @DisplayName("requirePagePermission is a no-op when the page has no resolvable permission")
    void requirePagePermissionNoOpWhenUnresolvable() {
        UIPage page = UIPage.builder().pageCode(null).permissionCode(null).build();

        authorizationService.requirePagePermission(page);

        verifyNoInteractions(rolePermissionRepository);
    }

    @Test
    @DisplayName("requirePagePermission enforces the resolved permission and throws when denied")
    void requirePagePermissionEnforcesResolvedPermission() {
        authenticateAs("ROLE_GATE_OPERATOR");
        when(rolePermissionRepository.findPatternsByRoleCode("ROLE_GATE_OPERATOR"))
                .thenReturn(List.of("dock.*"));

        UIPage page = UIPage.builder().pageCode("GATE_CHECKIN").permissionCode(null).build();

        assertThatThrownBy(() -> authorizationService.requirePagePermission(page))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("gate.checkin");
    }
}
