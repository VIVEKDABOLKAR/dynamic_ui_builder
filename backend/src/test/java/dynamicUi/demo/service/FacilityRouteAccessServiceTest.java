package dynamicUi.demo.service;

import dynamicUi.demo.dto.RouteAccessResponse;
import dynamicUi.demo.entity.Facility;
import dynamicUi.demo.entity.FacilityRouteAccess;
import dynamicUi.demo.entity.UIPage;
import dynamicUi.demo.entity.UIRoute;
import dynamicUi.demo.repoistory.FacilityRepository;
import dynamicUi.demo.repoistory.FacilityRouteAccessRepository;
import dynamicUi.demo.repoistory.UIRouteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link FacilityRouteAccessService}.
 */
@ExtendWith(MockitoExtension.class)
class FacilityRouteAccessServiceTest {

    @Mock
    private FacilityRouteAccessRepository accessRepository;

    @Mock
    private UIRouteRepository uiRouteRepository;

    @Mock
    private FacilityRepository facilityRepository;

    @InjectMocks
    private FacilityRouteAccessService service;

    private UIRoute route(long id, String code, String path, int order) {
        UIPage page = UIPage.builder().pageName(code + "_PAGE").build();
        return UIRoute.builder().id(id).routeCode(code).path(path).menuOrder(order).page(page).build();
    }

    // ── getAccessForFacility ─────────────────────────────────────────────

    @Test
    @DisplayName("getAccessForFacility throws 404 when the facility does not exist")
    void getAccessForFacilityThrowsNotFound() {
        when(facilityRepository.existsById("F9")).thenReturn(false);

        assertThatThrownBy(() -> service.getAccessForFacility("F9"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("F9");
    }

    @Test
    @DisplayName("getAccessForFacility marks only actively granted routes as granted")
    void getAccessForFacilityMarksGrantedRoutes() {
        when(facilityRepository.existsById("F1")).thenReturn(true);
        when(accessRepository.findByFacilityIdAndActiveTrue("F1"))
                .thenReturn(List.of(FacilityRouteAccess.builder().facilityId("F1").routeId(1L).active(true).build()));
        when(uiRouteRepository.findByIsActiveTrueOrderByMenuOrderAsc())
                .thenReturn(List.of(route(1L, "R1", "/r1", 1), route(2L, "R2", "/r2", 2)));

        RouteAccessResponse response = service.getAccessForFacility("F1");

        assertThat(response.getFacilityId()).isEqualTo("F1");
        assertThat(response.isGlobal()).isFalse();
        assertThat(response.getRoutes()).hasSize(2);
        assertThat(response.getRoutes().get(0).isGranted()).isTrue();
        assertThat(response.getRoutes().get(1).isGranted()).isFalse();
    }

    // ── getGlobalAccess ──────────────────────────────────────────────────

    @Test
    @DisplayName("getGlobalAccess returns no granted routes when there are no facilities")
    void getGlobalAccessEmptyWhenNoFacilities() {
        when(facilityRepository.findAll()).thenReturn(List.of());
        when(uiRouteRepository.findByIsActiveTrueOrderByMenuOrderAsc())
                .thenReturn(List.of(route(1L, "R1", "/r1", 1)));

        RouteAccessResponse response = service.getGlobalAccess();

        assertThat(response.isGlobal()).isTrue();
        assertThat(response.getFacilityId()).isNull();
        assertThat(response.getRoutes()).allMatch(r -> !r.isGranted());
    }

    @Test
    @DisplayName("getGlobalAccess shows only routes granted to every facility (intersection)")
    void getGlobalAccessShowsIntersection() {
        Facility f1 = Facility.builder().id("F1").name("Facility 1").build();
        Facility f2 = Facility.builder().id("F2").name("Facility 2").build();
        when(facilityRepository.findAll()).thenReturn(List.of(f1, f2));

        when(accessRepository.findByFacilityIdAndActiveTrue("F1"))
                .thenReturn(List.of(
                        FacilityRouteAccess.builder().facilityId("F1").routeId(1L).active(true).build(),
                        FacilityRouteAccess.builder().facilityId("F1").routeId(2L).active(true).build()));
        when(accessRepository.findByFacilityIdAndActiveTrue("F2"))
                .thenReturn(List.of(
                        FacilityRouteAccess.builder().facilityId("F2").routeId(2L).active(true).build()));

        when(uiRouteRepository.findByIsActiveTrueOrderByMenuOrderAsc())
                .thenReturn(List.of(route(1L, "R1", "/r1", 1), route(2L, "R2", "/r2", 2)));

        RouteAccessResponse response = service.getGlobalAccess();

        assertThat(response.getRoutes())
                .filteredOn(r -> r.getRouteId() == 1L)
                .allMatch(r -> !r.isGranted());
        assertThat(response.getRoutes())
                .filteredOn(r -> r.getRouteId() == 2L)
                .allMatch(dynamicUi.demo.dto.RouteAccessDTO::isGranted);
    }

    // ── updateFacilityAccess ─────────────────────────────────────────────

    @Test
    @DisplayName("updateFacilityAccess throws 404 for an unknown facility")
    void updateFacilityAccessThrowsNotFoundForUnknownFacility() {
        when(facilityRepository.existsById("F9")).thenReturn(false);

        assertThatThrownBy(() -> service.updateFacilityAccess("F9", List.of(1L)))
                .isInstanceOf(ResponseStatusException.class);
        verify(accessRepository, never()).findByFacilityId(any());
    }

    @Test
    @DisplayName("updateFacilityAccess throws 400 for an unknown route id")
    void updateFacilityAccessThrowsBadRequestForUnknownRoute() {
        when(facilityRepository.existsById("F1")).thenReturn(true);
        when(uiRouteRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.updateFacilityAccess("F1", List.of(99L)))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @DisplayName("updateFacilityAccess creates new rows for newly selected routes")
    void updateFacilityAccessCreatesNewRows() {
        when(facilityRepository.existsById("F1")).thenReturn(true);
        when(uiRouteRepository.existsById(1L)).thenReturn(true);
        when(accessRepository.findByFacilityId("F1")).thenReturn(List.of());

        service.updateFacilityAccess("F1", List.of(1L));

        ArgumentCaptor<FacilityRouteAccess> captor = ArgumentCaptor.forClass(FacilityRouteAccess.class);
        verify(accessRepository).save(captor.capture());
        assertThat(captor.getValue().getFacilityId()).isEqualTo("F1");
        assertThat(captor.getValue().getRouteId()).isEqualTo(1L);
        assertThat(captor.getValue().isActive()).isTrue();
    }

    @Test
    @DisplayName("updateFacilityAccess re-activates an existing inactive row instead of duplicating it")
    void updateFacilityAccessReactivatesInactiveRow() {
        when(facilityRepository.existsById("F1")).thenReturn(true);
        when(uiRouteRepository.existsById(1L)).thenReturn(true);
        FacilityRouteAccess inactive = FacilityRouteAccess.builder()
                .id(10L).facilityId("F1").routeId(1L).active(false).build();
        when(accessRepository.findByFacilityId("F1")).thenReturn(List.of(inactive));

        service.updateFacilityAccess("F1", List.of(1L));

        ArgumentCaptor<FacilityRouteAccess> captor = ArgumentCaptor.forClass(FacilityRouteAccess.class);
        verify(accessRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(10L);
        assertThat(captor.getValue().isActive()).isTrue();
    }

    @Test
    @DisplayName("updateFacilityAccess revokes active rows that are no longer selected")
    void updateFacilityAccessRevokesUnselectedRows() {
        when(facilityRepository.existsById("F1")).thenReturn(true);
        FacilityRouteAccess active = FacilityRouteAccess.builder()
                .id(11L).facilityId("F1").routeId(2L).active(true).build();
        when(accessRepository.findByFacilityId("F1")).thenReturn(List.of(active));

        service.updateFacilityAccess("F1", List.of());

        ArgumentCaptor<FacilityRouteAccess> captor = ArgumentCaptor.forClass(FacilityRouteAccess.class);
        verify(accessRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(11L);
        assertThat(captor.getValue().isActive()).isFalse();
    }

    @Test
    @DisplayName("updateFacilityAccess with a null selection revokes all currently active routes")
    void updateFacilityAccessWithNullSelectionRevokesAll() {
        when(facilityRepository.existsById("F1")).thenReturn(true);
        FacilityRouteAccess active = FacilityRouteAccess.builder()
                .id(11L).facilityId("F1").routeId(2L).active(true).build();
        when(accessRepository.findByFacilityId("F1")).thenReturn(List.of(active));

        service.updateFacilityAccess("F1", null);

        ArgumentCaptor<FacilityRouteAccess> captor = ArgumentCaptor.forClass(FacilityRouteAccess.class);
        verify(accessRepository).save(captor.capture());
        assertThat(captor.getValue().isActive()).isFalse();
    }

    @Test
    @DisplayName("updateFacilityAccess does not re-save a row that is already active and selected")
    void updateFacilityAccessLeavesAlreadyActiveRowUntouched() {
        when(facilityRepository.existsById("F1")).thenReturn(true);
        when(uiRouteRepository.existsById(1L)).thenReturn(true);
        FacilityRouteAccess active = FacilityRouteAccess.builder()
                .id(11L).facilityId("F1").routeId(1L).active(true).build();
        when(accessRepository.findByFacilityId("F1")).thenReturn(List.of(active));

        service.updateFacilityAccess("F1", List.of(1L));

        verify(accessRepository, never()).save(any());
    }

    // ── updateGlobalAccess ───────────────────────────────────────────────

    @Test
    @DisplayName("updateGlobalAccess applies the same route selection to every facility")
    void updateGlobalAccessAppliesToEveryFacility() {
        Facility f1 = Facility.builder().id("F1").name("Facility 1").build();
        Facility f2 = Facility.builder().id("F2").name("Facility 2").build();
        when(facilityRepository.findAll()).thenReturn(List.of(f1, f2));
        when(uiRouteRepository.existsById(1L)).thenReturn(true);
        when(accessRepository.findByFacilityId("F1")).thenReturn(List.of());
        when(accessRepository.findByFacilityId("F2")).thenReturn(List.of());

        service.updateGlobalAccess(List.of(1L));

        ArgumentCaptor<FacilityRouteAccess> captor = ArgumentCaptor.forClass(FacilityRouteAccess.class);
        verify(accessRepository, times(2)).save(captor.capture());
        assertThat(captor.getAllValues())
                .extracting(FacilityRouteAccess::getFacilityId)
                .containsExactlyInAnyOrder("F1", "F2");
    }

    @Test
    @DisplayName("updateGlobalAccess with no facilities makes no writes")
    void updateGlobalAccessWithNoFacilitiesMakesNoWrites() {
        when(facilityRepository.findAll()).thenReturn(List.of());

        service.updateGlobalAccess(List.of(1L));

        verify(accessRepository, never()).save(any());
    }
}
