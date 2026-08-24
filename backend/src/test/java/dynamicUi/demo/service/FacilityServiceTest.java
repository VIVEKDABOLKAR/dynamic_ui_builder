package dynamicUi.demo.service;

import dynamicUi.demo.entity.Facility;
import dynamicUi.demo.repoistory.FacilityRepository;
import dynamicUi.demo.security.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link FacilityService}.
 */
@ExtendWith(MockitoExtension.class)
class FacilityServiceTest {

    @Mock
    private FacilityRepository facilityRepository;

    @Mock
    private JwtUtil jwtUtilService;

    @InjectMocks
    private FacilityService facilityService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAs(String username, String... authorities) {
        var granted = java.util.Arrays.stream(authorities)
                .map(SimpleGrantedAuthority::new)
                .toList();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, "N/A", granted));
    }

    // ── findAll / findAccessibleFacilities ──────────────────────────────

    @Test
    @DisplayName("findAll delegates to the repository")
    void findAllDelegatesToRepository() {
        List<Facility> facilities = List.of(Facility.builder().id("F1").name("Facility 1").build());
        when(facilityRepository.findAll()).thenReturn(facilities);

        assertThat(facilityService.findAll()).isEqualTo(facilities);
    }

    @Test
    @DisplayName("findAccessibleFacilities returns every facility for a ROLE_ADMIN user")
    void findAccessibleFacilitiesReturnsAllForAdmin() {
        authenticateAs("admin-user", "ROLE_ADMIN");
        List<Facility> all = List.of(
                Facility.builder().id("F1").name("Facility 1").build(),
                Facility.builder().id("F2").name("Facility 2").build());
        when(facilityRepository.findAll()).thenReturn(all);

        List<Facility> result = facilityService.findAccessibleFacilities("admin-user");

        assertThat(result).isEqualTo(all);
        verify(facilityRepository, never()).findAccessibleFacilitiesByUsername(any());
    }

    @Test
    @DisplayName("findAccessibleFacilities scopes to granted facilities for a non-admin user")
    void findAccessibleFacilitiesScopesForNonAdmin() {
        authenticateAs("viewer-user", "ROLE_VIEWER");
        List<Facility> accessible = List.of(Facility.builder().id("F1").name("Facility 1").build());
        when(facilityRepository.findAccessibleFacilitiesByUsername("viewer-user")).thenReturn(accessible);

        List<Facility> result = facilityService.findAccessibleFacilities("viewer-user");

        assertThat(result).isEqualTo(accessible);
        verify(facilityRepository, never()).findAll();
    }

    // ── changeFacility ───────────────────────────────────────────────────

    @Test
    @DisplayName("changeFacility issues a new token when the facility is accessible")
    void changeFacilityIssuesTokenWhenAllowed() {
        authenticateAs("viewer-user", "ROLE_VIEWER");
        when(facilityRepository.findAccessibleFacilitiesByUsername("viewer-user"))
                .thenReturn(List.of(Facility.builder().id("F1").name("Facility 1").build()));
        when(jwtUtilService.generateToken("old-token", "F1")).thenReturn("new-token");

        String result = facilityService.changeFacility("old-token", "F1");

        assertThat(result).isEqualTo("new-token");
    }

    @Test
    @DisplayName("changeFacility returns null when the facility is not accessible to the user")
    void changeFacilityReturnsNullWhenNotAllowed() {
        authenticateAs("viewer-user", "ROLE_VIEWER");
        when(facilityRepository.findAccessibleFacilitiesByUsername("viewer-user"))
                .thenReturn(List.of(Facility.builder().id("F1").name("Facility 1").build()));

        String result = facilityService.changeFacility("old-token", "F2");

        assertThat(result).isNull();
        verify(jwtUtilService, never()).generateToken(any(), any());
    }

    // ── createFacility ───────────────────────────────────────────────────

    @Test
    @DisplayName("createFacility normalizes the ID to uppercase and trims the name")
    void createFacilityNormalizesIdAndTrimsName() {
        when(facilityRepository.existsById("F1")).thenReturn(false);
        when(facilityRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Facility payload = Facility.builder().id("f1").name("  My Facility  ").build();
        Facility result = facilityService.createFacility(payload);

        assertThat(result.getId()).isEqualTo("F1");
        assertThat(result.getName()).isEqualTo("My Facility");
    }

    @Test
    @DisplayName("createFacility throws 400 when the ID is missing")
    void createFacilityThrowsBadRequestForMissingId() {
        Facility payload = Facility.builder().id(null).name("My Facility").build();

        assertThatThrownBy(() -> facilityService.createFacility(payload))
                .isInstanceOf(ResponseStatusException.class);
        verify(facilityRepository, never()).save(any());
    }

    @Test
    @DisplayName("createFacility throws 400 when the name is missing")
    void createFacilityThrowsBadRequestForMissingName() {
        Facility payload = Facility.builder().id("F1").name(" ").build();

        assertThatThrownBy(() -> facilityService.createFacility(payload))
                .isInstanceOf(ResponseStatusException.class);
        verify(facilityRepository, never()).save(any());
    }

    @Test
    @DisplayName("createFacility throws 409 when a facility with the same normalized ID exists")
    void createFacilityThrowsConflictWhenIdExists() {
        when(facilityRepository.existsById("F1")).thenReturn(true);
        Facility payload = Facility.builder().id("f1").name("Duplicate").build();

        assertThatThrownBy(() -> facilityService.createFacility(payload))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("F1");
        verify(facilityRepository, never()).save(any());
    }

    // ── updateFacility ───────────────────────────────────────────────────

    @Test
    @DisplayName("updateFacility updates the name of an existing facility")
    void updateFacilityUpdatesName() {
        Facility existing = Facility.builder().id("F1").name("Old Name").build();
        when(facilityRepository.findById("F1")).thenReturn(Optional.of(existing));
        when(facilityRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Facility result = facilityService.updateFacility("F1", Facility.builder().name(" New Name ").build());

        assertThat(result.getName()).isEqualTo("New Name");
    }

    @Test
    @DisplayName("updateFacility throws 404 when the facility does not exist")
    void updateFacilityThrowsNotFound() {
        when(facilityRepository.findById("F9")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> facilityService.updateFacility("F9", Facility.builder().name("X").build()))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @DisplayName("updateFacility throws 400 when the new name is blank")
    void updateFacilityThrowsBadRequestForBlankName() {
        Facility existing = Facility.builder().id("F1").name("Old Name").build();
        when(facilityRepository.findById("F1")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> facilityService.updateFacility("F1", Facility.builder().name("  ").build()))
                .isInstanceOf(ResponseStatusException.class);
        verify(facilityRepository, never()).save(any());
    }

    // ── deleteFacility ───────────────────────────────────────────────────

    @Test
    @DisplayName("deleteFacility removes an existing facility")
    void deleteFacilityRemovesExisting() {
        when(facilityRepository.existsById("F1")).thenReturn(true);

        facilityService.deleteFacility("F1");

        verify(facilityRepository).deleteById("F1");
    }

    @Test
    @DisplayName("deleteFacility throws 404 when the facility does not exist")
    void deleteFacilityThrowsNotFound() {
        when(facilityRepository.existsById("F9")).thenReturn(false);

        assertThatThrownBy(() -> facilityService.deleteFacility("F9"))
                .isInstanceOf(ResponseStatusException.class);
        verify(facilityRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("deleteFacility throws 409 when the facility is referenced by other records")
    void deleteFacilityThrowsConflictOnIntegrityViolation() {
        when(facilityRepository.existsById("F1")).thenReturn(true);
        doThrow(new DataIntegrityViolationException("FK violation"))
                .when(facilityRepository).deleteById("F1");

        assertThatThrownBy(() -> facilityService.deleteFacility("F1"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("F1");
    }
}
