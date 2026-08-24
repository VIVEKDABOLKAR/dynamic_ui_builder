package dynamicUi.demo.service;

import dynamicUi.demo.dto.NavigationNodeDTO;
import dynamicUi.demo.entity.UIPage;
import dynamicUi.demo.entity.UIRoute;
import dynamicUi.demo.repoistory.FacilityRouteAccessRepository;
import dynamicUi.demo.repoistory.UIRouteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link NavigationBuilderService}.
 */
@ExtendWith(MockitoExtension.class)
class NavigationBuilderServiceTest {

    @Mock
    private UIRouteRepository uiRouteRepository;

    @Mock
    private FacilityRouteAccessRepository facilityRouteAccessRepository;

    @InjectMocks
    private NavigationBuilderService service;

    private UIRoute route(long id, String routeCode, String path, String parentMenu, int order, String pageName) {
        UIPage page = UIPage.builder().pageName(pageName).pageCode(routeCode + "_CODE").build();
        return UIRoute.builder()
                .id(id).routeCode(routeCode).path(path).parentMenu(parentMenu)
                .menuOrder(order).page(page).build();
    }

    @Test
    @DisplayName("buildSidebar returns nothing for a null or blank facilityId")
    void buildSidebarEmptyForBlankFacility() {
        assertThat(service.buildSidebar(null)).isEmpty();
        assertThat(service.buildSidebar("  ")).isEmpty();
        verify(facilityRouteAccessRepository, never()).existsByFacilityId(any());
    }

    @Test
    @DisplayName("buildSidebar returns nothing for the GLOBAL pseudo-facility")
    void buildSidebarEmptyForGlobal() {
        assertThat(service.buildSidebar("GLOBAL")).isEmpty();
        verify(facilityRouteAccessRepository, never()).existsByFacilityId(any());
    }

    @Test
    @DisplayName("buildSidebar returns nothing when the facility has no access rows configured")
    void buildSidebarEmptyWhenFacilityUnconfigured() {
        when(facilityRouteAccessRepository.existsByFacilityId("F1")).thenReturn(false);

        assertThat(service.buildSidebar("F1")).isEmpty();
        verify(uiRouteRepository, never()).findByIsActiveTrueAndShowInMenuTrueAndIdInOrderByMenuOrderAsc(any());
    }

    @Test
    @DisplayName("buildSidebar returns nothing when the facility has zero granted routes")
    void buildSidebarEmptyWhenNoGrantedRoutes() {
        when(facilityRouteAccessRepository.existsByFacilityId("F1")).thenReturn(true);
        when(facilityRouteAccessRepository.findRouteIdsByFacilityIdAndActiveTrue("F1")).thenReturn(List.of());

        assertThat(service.buildSidebar("F1")).isEmpty();
        verify(uiRouteRepository, never()).findByIsActiveTrueAndShowInMenuTrueAndIdInOrderByMenuOrderAsc(any());
    }

    @Test
    @DisplayName("buildSidebar produces top-level leaf nodes for routes with no parent menu")
    void buildSidebarProducesLeafNodes() {
        when(facilityRouteAccessRepository.existsByFacilityId("F1")).thenReturn(true);
        when(facilityRouteAccessRepository.findRouteIdsByFacilityIdAndActiveTrue("F1"))
                .thenReturn(List.of(1L, 2L));
        when(uiRouteRepository.findByIsActiveTrueAndShowInMenuTrueAndIdInOrderByMenuOrderAsc(List.of(1L, 2L)))
                .thenReturn(List.of(
                        route(1L, "DASHBOARD_ROUTE", "/dashboard", null, 1, "Dashboard"),
                        route(2L, "REPORTS_ROUTE", "/reports", null, 2, "Reports")));

        List<NavigationNodeDTO> nodes = service.buildSidebar("F1");

        assertThat(nodes).hasSize(2);
        assertThat(nodes.get(0).getLabel()).isEqualTo("Dashboard");
        assertThat(nodes.get(0).getPath()).isEqualTo("/dashboard");
        assertThat(nodes.get(0).getChildren()).isEmpty();
        assertThat(nodes.get(1).getLabel()).isEqualTo("Reports");
    }

    @Test
    @DisplayName("buildSidebar groups routes sharing a parentMenu under one group node")
    void buildSidebarGroupsByParentMenu() {
        when(facilityRouteAccessRepository.existsByFacilityId("F1")).thenReturn(true);
        when(facilityRouteAccessRepository.findRouteIdsByFacilityIdAndActiveTrue("F1"))
                .thenReturn(List.of(1L, 2L));
        when(uiRouteRepository.findByIsActiveTrueAndShowInMenuTrueAndIdInOrderByMenuOrderAsc(List.of(1L, 2L)))
                .thenReturn(List.of(
                        route(1L, "GATE_CHECKIN_ROUTE", "/gate/check-in", "Gate", 2, "Check In"),
                        route(2L, "GATE_CHECKOUT_ROUTE", "/gate/check-out", "Gate", 1, "Check Out")));

        List<NavigationNodeDTO> nodes = service.buildSidebar("F1");

        assertThat(nodes).hasSize(1);
        NavigationNodeDTO group = nodes.get(0);
        assertThat(group.getLabel()).isEqualTo("Gate");
        assertThat(group.getPath()).isNull();
        assertThat(group.getChildren()).hasSize(2);
        // children ordered by their own menuOrder: Check Out (1) before Check In (2)
        assertThat(group.getChildren().get(0).getLabel()).isEqualTo("Check Out");
        assertThat(group.getChildren().get(1).getLabel()).isEqualTo("Check In");
        // group's own order is the min of its children's order
        assertThat(group.getMenuOrder()).isEqualTo(1);
    }

    @Test
    @DisplayName("buildSidebar sorts top-level nodes (leaves and groups) by menuOrder")
    void buildSidebarSortsTopLevelNodesByMenuOrder() {
        when(facilityRouteAccessRepository.existsByFacilityId("F1")).thenReturn(true);
        when(facilityRouteAccessRepository.findRouteIdsByFacilityIdAndActiveTrue("F1"))
                .thenReturn(List.of(1L, 2L, 3L));
        when(uiRouteRepository.findByIsActiveTrueAndShowInMenuTrueAndIdInOrderByMenuOrderAsc(List.of(1L, 2L, 3L)))
                .thenReturn(List.of(
                        route(1L, "DASHBOARD_ROUTE", "/dashboard", null, 5, "Dashboard"),
                        route(2L, "GATE_CHECKIN_ROUTE", "/gate/check-in", "Gate", 1, "Check In"),
                        route(3L, "REPORTS_ROUTE", "/reports", null, 3, "Reports")));

        List<NavigationNodeDTO> nodes = service.buildSidebar("F1");

        assertThat(nodes).extracting(NavigationNodeDTO::getLabel)
                .containsExactly("Gate", "Reports", "Dashboard");
    }
}
