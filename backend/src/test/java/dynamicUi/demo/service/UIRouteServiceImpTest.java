package dynamicUi.demo.service;

import dynamicUi.demo.dto.RouteResponseDTO;
import dynamicUi.demo.entity.UIPage;
import dynamicUi.demo.entity.UIRoute;
import dynamicUi.demo.mapper.UIRouteMapper;
import dynamicUi.demo.repoistory.FacilityRouteAccessRepository;
import dynamicUi.demo.repoistory.UIRouteRepository;
import dynamicUi.demo.service.inter.AuthorizationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UIRouteServiceImp}.
 */
@ExtendWith(MockitoExtension.class)
class UIRouteServiceImpTest {

    @Mock
    private UIRouteRepository repository;

    @Mock
    private UIRouteMapper uiRouteMapper;

    @Mock
    private FacilityRouteAccessRepository facilityRouteAccessRepository;

    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private UIRouteServiceImp service;

    private UIRoute activeRoute(long id, String path) {
        UIPage page = UIPage.builder().pageCode("GATE_CHECKIN").pageName("Gate Check-in").build();
        return UIRoute.builder().id(id).path(path).isActive(true).page(page).build();
    }

    // ── resolveByPath ────────────────────────────────────────────────────

    @Test
    @DisplayName("resolveByPath returns the mapped DTO for an active route")
    void resolveByPathReturnsMappedDtoForActiveRoute() {
        UIRoute route = activeRoute(1L, "/gate/check-in");
        RouteResponseDTO dto = RouteResponseDTO.builder().path("/gate/check-in").build();
        when(repository.findByPath("/gate/check-in")).thenReturn(Optional.of(route));
        when(uiRouteMapper.toResponse(route)).thenReturn(dto);

        assertThat(service.resolveByPath("/gate/check-in")).isEqualTo(dto);
    }

    @Test
    @DisplayName("resolveByPath throws when no route matches the path")
    void resolveByPathThrowsWhenNotFound() {
        when(repository.findByPath("/missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.resolveByPath("/missing"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("resolveByPath throws when the route is inactive")
    void resolveByPathThrowsWhenInactive() {
        UIRoute route = activeRoute(1L, "/gate/check-in");
        route.setIsActive(false);
        when(repository.findByPath("/gate/check-in")).thenReturn(Optional.of(route));

        assertThatThrownBy(() -> service.resolveByPath("/gate/check-in"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("NOT ACTIVE");
    }

    // ── resolveByPathAndFacility ─────────────────────────────────────────

    @Test
    @DisplayName("resolveByPathAndFacility returns the mapped DTO when access and permission are granted")
    void resolveByPathAndFacilityReturnsDtoWhenAllowed() {
        UIRoute route = activeRoute(1L, "/gate/check-in");
        RouteResponseDTO dto = RouteResponseDTO.builder().path("/gate/check-in").build();
        when(repository.findByPath("/gate/check-in")).thenReturn(Optional.of(route));
        when(facilityRouteAccessRepository.existsByFacilityIdAndRouteIdAndActiveTrue("F1", 1L)).thenReturn(true);
        when(uiRouteMapper.toResponse(route)).thenReturn(dto);

        assertThat(service.resolveByPathAndFacility("/gate/check-in", "F1")).isEqualTo(dto);
        verify(authorizationService).requirePagePermission(route.getPage());
    }

    @Test
    @DisplayName("resolveByPathAndFacility throws 403 when the facility has no access to the route")
    void resolveByPathAndFacilityThrowsForbiddenWhenNoAccess() {
        UIRoute route = activeRoute(1L, "/gate/check-in");
        when(repository.findByPath("/gate/check-in")).thenReturn(Optional.of(route));
        when(facilityRouteAccessRepository.existsByFacilityIdAndRouteIdAndActiveTrue("F1", 1L)).thenReturn(false);

        assertThatThrownBy(() -> service.resolveByPathAndFacility("/gate/check-in", "F1"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.FORBIDDEN));

        verify(authorizationService, never()).requirePagePermission(any());
    }

    @Test
    @DisplayName("resolveByPathAndFacility propagates a 403 from the authorization service")
    void resolveByPathAndFacilityPropagatesPermissionDenial() {
        UIRoute route = activeRoute(1L, "/gate/check-in");
        when(repository.findByPath("/gate/check-in")).thenReturn(Optional.of(route));
        when(facilityRouteAccessRepository.existsByFacilityIdAndRouteIdAndActiveTrue("F1", 1L)).thenReturn(true);
        org.mockito.Mockito.doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "denied"))
                .when(authorizationService).requirePagePermission(route.getPage());

        assertThatThrownBy(() -> service.resolveByPathAndFacility("/gate/check-in", "F1"))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @DisplayName("resolveByPathAndFacility throws when the route is inactive, after access/permission checks pass")
    void resolveByPathAndFacilityThrowsWhenInactive() {
        UIRoute route = activeRoute(1L, "/gate/check-in");
        route.setIsActive(false);
        when(repository.findByPath("/gate/check-in")).thenReturn(Optional.of(route));
        when(facilityRouteAccessRepository.existsByFacilityIdAndRouteIdAndActiveTrue("F1", 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.resolveByPathAndFacility("/gate/check-in", "F1"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("NOT ACTIVE");
    }

    // ── createRoute ──────────────────────────────────────────────────────

    @Test
    @DisplayName("createRoute saves a new route when the code and path are unique")
    void createRouteSavesWhenUnique() {
        UIRoute route = UIRoute.builder().routeCode("R1").path("/r1").build();
        when(repository.existsByRouteCode("R1")).thenReturn(false);
        when(repository.existsByPath("/r1")).thenReturn(false);
        when(repository.save(route)).thenReturn(route);

        assertThat(service.createRoute(route)).isEqualTo(route);
    }

    @Test
    @DisplayName("createRoute throws when the route code already exists")
    void createRouteThrowsForDuplicateCode() {
        UIRoute route = UIRoute.builder().routeCode("R1").path("/r1").build();
        when(repository.existsByRouteCode("R1")).thenReturn(true);

        assertThatThrownBy(() -> service.createRoute(route)).isInstanceOf(RuntimeException.class);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("createRoute throws when the path already exists")
    void createRouteThrowsForDuplicatePath() {
        UIRoute route = UIRoute.builder().routeCode("R1").path("/r1").build();
        when(repository.existsByRouteCode("R1")).thenReturn(false);
        when(repository.existsByPath("/r1")).thenReturn(true);

        assertThatThrownBy(() -> service.createRoute(route)).isInstanceOf(RuntimeException.class);
        verify(repository, never()).save(any());
    }

    // ── updateRoute ──────────────────────────────────────────────────────

    @Test
    @DisplayName("updateRoute overwrites mutable fields on the existing route")
    void updateRouteOverwritesFields() {
        UIRoute existing = UIRoute.builder().id(1L).routeCode("R1").path("/old").isActive(true).build();
        UIRoute request = UIRoute.builder()
                .path("/new").showInMenu(false).parentMenu("Gate")
                .menuOrder(3).breadcrumb(false).icon("fa-icon").isActive(false)
                .build();
        when(repository.findByRouteCode("R1")).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        UIRoute result = service.updateRoute("R1", request);

        assertThat(result.getPath()).isEqualTo("/new");
        assertThat(result.getShowInMenu()).isFalse();
        assertThat(result.getParentMenu()).isEqualTo("Gate");
        assertThat(result.getMenuOrder()).isEqualTo(3);
        assertThat(result.getBreadcrumb()).isFalse();
        assertThat(result.getIcon()).isEqualTo("fa-icon");
        assertThat(result.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("updateRoute throws when the route code does not exist")
    void updateRouteThrowsWhenNotFound() {
        when(repository.findByRouteCode("MISSING")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateRoute("MISSING", new UIRoute()))
                .isInstanceOf(RuntimeException.class);
    }

    // ── getRoute / getAllRoutes ──────────────────────────────────────────

    @Test
    @DisplayName("getRoute returns the route when found")
    void getRouteReturnsWhenFound() {
        UIRoute route = UIRoute.builder().id(1L).routeCode("R1").build();
        when(repository.findByRouteCode("R1")).thenReturn(Optional.of(route));

        assertThat(service.getRoute("R1")).isEqualTo(route);
    }

    @Test
    @DisplayName("getRoute throws when not found")
    void getRouteThrowsWhenNotFound() {
        when(repository.findByRouteCode("MISSING")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getRoute("MISSING")).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("getAllRoutes delegates to the active-and-ordered repository query")
    void getAllRoutesDelegates() {
        List<UIRoute> routes = List.of(UIRoute.builder().id(1L).routeCode("R1").build());
        when(repository.findByIsActiveTrueOrderByMenuOrderAsc()).thenReturn(routes);

        assertThat(service.getAllRoutes()).isEqualTo(routes);
    }

    // ── deleteRoute ──────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteRoute soft-deletes by flipping isActive to false")
    void deleteRouteSoftDeletes() {
        UIRoute route = UIRoute.builder().id(1L).routeCode("R1").isActive(true).build();
        when(repository.findByRouteCode("R1")).thenReturn(Optional.of(route));
        when(repository.save(route)).thenReturn(route);

        service.deleteRoute("R1");

        assertThat(route.getIsActive()).isFalse();
        verify(repository, times(1)).save(route);
    }

    @Test
    @DisplayName("deleteRoute throws when the route code does not exist")
    void deleteRouteThrowsWhenNotFound() {
        when(repository.findByRouteCode("MISSING")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteRoute("MISSING")).isInstanceOf(RuntimeException.class);
        verify(repository, never()).save(any());
    }
}
