package dynamicUi.demo.service;

import dynamicUi.demo.dto.RoleAccessResponse;
import dynamicUi.demo.entity.AppRole;
import dynamicUi.demo.entity.RolePermission;
import dynamicUi.demo.repoistory.AppRoleRepository;
import dynamicUi.demo.repoistory.RolePermissionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link RolePermissionAdminService}.
 */
@ExtendWith(MockitoExtension.class)
class RolePermissionAdminServiceTest {

    @Mock
    private AppRoleRepository appRoleRepository;

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    @InjectMocks
    private RolePermissionAdminService service;

    private AppRole role(long id, String code) {
        return AppRole.builder().id(id).code(code).name(code).build();
    }

    // ── getAllRoles / getRole ───────────────────────────────────────────

    @Test
    @DisplayName("getAllRoles maps every role to its patterns")
    void getAllRolesMapsPatterns() {
        AppRole admin = role(1L, "ROLE_ADMIN");
        AppRole viewer = role(2L, "ROLE_VIEWER");
        when(appRoleRepository.findAll()).thenReturn(List.of(admin, viewer));
        when(rolePermissionRepository.findByAppRole_Code("ROLE_ADMIN"))
                .thenReturn(List.of(RolePermission.builder().permissionPattern("*").build()));
        when(rolePermissionRepository.findByAppRole_Code("ROLE_VIEWER"))
                .thenReturn(List.of());

        List<RoleAccessResponse> result = service.getAllRoles();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCode()).isEqualTo("ROLE_ADMIN");
        assertThat(result.get(0).getPatterns()).containsExactly("*");
        assertThat(result.get(1).getPatterns()).isEmpty();
    }

    @Test
    @DisplayName("getRole throws 404 when the role code does not exist")
    void getRoleThrowsNotFound() {
        when(appRoleRepository.findByCode("ROLE_MISSING")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getRole("ROLE_MISSING"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("ROLE_MISSING");
    }

    @Test
    @DisplayName("getRole returns the role's patterns when found")
    void getRoleReturnsPatterns() {
        AppRole admin = role(1L, "ROLE_ADMIN");
        when(appRoleRepository.findByCode("ROLE_ADMIN")).thenReturn(Optional.of(admin));
        when(rolePermissionRepository.findByAppRole_Code("ROLE_ADMIN"))
                .thenReturn(List.of(RolePermission.builder().permissionPattern("*").build()));

        RoleAccessResponse response = service.getRole("ROLE_ADMIN");

        assertThat(response.getCode()).isEqualTo("ROLE_ADMIN");
        assertThat(response.getPatterns()).containsExactly("*");
    }

    // ── updatePatterns ───────────────────────────────────────────────────

    @Test
    @DisplayName("updatePatterns replaces existing patterns: deletes old rows then saves new ones")
    void updatePatternsReplacesExistingRows() {
        AppRole role = role(5L, "ROLE_GATE_OPERATOR");
        when(appRoleRepository.findByCode("ROLE_GATE_OPERATOR")).thenReturn(Optional.of(role));
        when(rolePermissionRepository.findByAppRole_Code("ROLE_GATE_OPERATOR"))
                .thenReturn(List.of(RolePermission.builder().permissionPattern("gate.*").build()));

        service.updatePatterns("ROLE_GATE_OPERATOR", List.of("gate.*", " dock.view "));

        verify(rolePermissionRepository).deleteByAppRole_Id(5L);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<RolePermission>> captor = ArgumentCaptor.forClass(List.class);
        verify(rolePermissionRepository).saveAll(captor.capture());

        List<RolePermission> saved = captor.getValue();
        assertThat(saved).extracting(RolePermission::getPermissionPattern)
                .containsExactly("gate.*", "dock.view");
        assertThat(saved).allMatch(rp -> rp.getAppRole() == role);
    }

    @Test
    @DisplayName("updatePatterns filters out null and blank pattern entries")
    void updatePatternsFiltersBlankEntries() {
        AppRole role = role(5L, "ROLE_GATE_OPERATOR");
        when(appRoleRepository.findByCode("ROLE_GATE_OPERATOR")).thenReturn(Optional.of(role));
        when(rolePermissionRepository.findByAppRole_Code("ROLE_GATE_OPERATOR")).thenReturn(List.of());

        service.updatePatterns("ROLE_GATE_OPERATOR", java.util.Arrays.asList("gate.*", null, "   ", ""));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<RolePermission>> captor = ArgumentCaptor.forClass(List.class);
        verify(rolePermissionRepository).saveAll(captor.capture());

        assertThat(captor.getValue()).extracting(RolePermission::getPermissionPattern)
                .containsExactly("gate.*");
    }

    @Test
    @DisplayName("updatePatterns with a null list deletes existing rows and saves nothing")
    void updatePatternsWithNullListOnlyDeletes() {
        AppRole role = role(5L, "ROLE_GATE_OPERATOR");
        when(appRoleRepository.findByCode("ROLE_GATE_OPERATOR")).thenReturn(Optional.of(role));
        when(rolePermissionRepository.findByAppRole_Code("ROLE_GATE_OPERATOR")).thenReturn(List.of());

        service.updatePatterns("ROLE_GATE_OPERATOR", null);

        verify(rolePermissionRepository).deleteByAppRole_Id(5L);
        verify(rolePermissionRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("updatePatterns throws 404 for an unknown role code")
    void updatePatternsThrowsNotFoundForUnknownRole() {
        when(appRoleRepository.findByCode("ROLE_MISSING")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updatePatterns("ROLE_MISSING", List.of("*")))
                .isInstanceOf(ResponseStatusException.class);

        verify(rolePermissionRepository, never()).deleteByAppRole_Id(any());
    }

    // ── createRole ───────────────────────────────────────────────────────

    @Test
    @DisplayName("createRole normalizes the code: uppercases, replaces spaces/dashes, prefixes ROLE_")
    void createRoleNormalizesCode() {
        when(appRoleRepository.existsByCode("ROLE_GATE_OPERATOR")).thenReturn(false);
        when(appRoleRepository.save(any())).thenAnswer(inv -> {
            AppRole saved = inv.getArgument(0);
            saved.setId(10L);
            return saved;
        });
        when(rolePermissionRepository.findByAppRole_Code("ROLE_GATE_OPERATOR")).thenReturn(List.of());

        RoleAccessResponse response = service.createRole("gate operator", null, null);

        assertThat(response.getCode()).isEqualTo("ROLE_GATE_OPERATOR");
        assertThat(response.getName()).isEqualTo("ROLE_GATE_OPERATOR"); // falls back to code when name blank
    }

    @Test
    @DisplayName("createRole keeps an already-prefixed code as-is (besides normalization)")
    void createRoleKeepsExistingPrefix() {
        when(appRoleRepository.existsByCode("ROLE_ADMIN")).thenReturn(false);
        when(appRoleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(rolePermissionRepository.findByAppRole_Code("ROLE_ADMIN")).thenReturn(List.of());

        RoleAccessResponse response = service.createRole("role_admin", "Administrator", "Full access");

        assertThat(response.getCode()).isEqualTo("ROLE_ADMIN");
        assertThat(response.getName()).isEqualTo("Administrator");
    }

    @Test
    @DisplayName("createRole throws 409 when the normalized code already exists")
    void createRoleThrowsConflictWhenExists() {
        when(appRoleRepository.existsByCode("ROLE_ADMIN")).thenReturn(true);

        assertThatThrownBy(() -> service.createRole("Admin", "Administrator", null))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("ROLE_ADMIN");

        verify(appRoleRepository, never()).save(any());
    }

    @Test
    @DisplayName("createRole throws 400 when the raw code is null or blank")
    void createRoleThrowsBadRequestForBlankCode() {
        assertThatThrownBy(() -> service.createRole(null, "Name", null))
                .isInstanceOf(ResponseStatusException.class);

        assertThatThrownBy(() -> service.createRole("   ", "Name", null))
                .isInstanceOf(ResponseStatusException.class);
    }
}
