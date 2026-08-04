package dynamicUi.demo.service;

import dynamicUi.demo.constant.FacilityId;
import dynamicUi.demo.dto.NavigationNodeDTO;
import dynamicUi.demo.entity.UIRoute;
import dynamicUi.demo.repoistory.FacilityRouteAccessRepository;
import dynamicUi.demo.repoistory.UIRouteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Phase 2 — Dynamic Navigation.
 *
 * Builds the sidebar tree purely from UIRoute rows:
 *
 *   UIRoute Table  →  Navigation Builder  →  Sidebar
 *
 * Grouping rule:
 *  - parentMenu == null/blank  → the route is a top-level, clickable leaf.
 *  - parentMenu == "Gate"      → the route becomes a child under a synthetic
 *                                "Gate" group node (group nodes have no path
 *                                of their own — they only exist to hold children).
 *
 * Ordering rule:
 *  - Children within a group are ordered by their own menuOrder.
 *  - Top-level nodes (leaves + groups) are ordered by the lowest menuOrder
 *    among their members, so a group sorts wherever its first child would.
 *
 * The router itself doesn't change — this only feeds the sidebar.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class NavigationBuilderService {

    private final UIRouteRepository uiRouteRepository;
    private final FacilityRouteAccessRepository facilityRouteAccessRepository;

    public List<NavigationNodeDTO> buildSidebar(String facilityId) {

//        List<UIRoute> routes =
//                uiRouteRepository.findByIsActiveTrueAndShowInMenuTrueOrderByMenuOrderAsc();

        List<UIRoute> routes = fetchRoutesForFacility(facilityId);

        // Split into top-level leaves vs. routes that belong to a named group
        List<UIRoute> topLevel = new ArrayList<>();
        Map<String, List<UIRoute>> byParentMenu = new LinkedHashMap<>();

        for (UIRoute route : routes) {
            String parentMenu = route.getParentMenu();
            if (parentMenu == null || parentMenu.isBlank()) {
                topLevel.add(route);
            } else {
                byParentMenu.computeIfAbsent(parentMenu, k -> new ArrayList<>()).add(route);
            }
        }

        List<NavigationNodeDTO> nodes = new ArrayList<>();

        for (UIRoute route : topLevel) {
            nodes.add(toLeafNode(route));
        }

        for (Map.Entry<String, List<UIRoute>> entry : byParentMenu.entrySet()) {
            nodes.add(toGroupNode(entry.getKey(), entry.getValue()));
        }

        // Sort top-level nodes by their own (or, for groups, their first child's) menuOrder
        nodes.sort(Comparator.comparing(
                n -> n.getMenuOrder() == null ? Integer.MAX_VALUE : n.getMenuOrder()));

        return nodes;
    }

    private NavigationNodeDTO toGroupNode(String groupName, List<UIRoute> children) {

        List<NavigationNodeDTO> childNodes = children.stream()
                .sorted(Comparator.comparing(
                        r -> r.getMenuOrder() == null ? Integer.MAX_VALUE : r.getMenuOrder()))
                .map(this::toLeafNode)
                .collect(Collectors.toList());

        Integer groupOrder = childNodes.stream()
                .map(NavigationNodeDTO::getMenuOrder)
                .filter(Objects::nonNull)
                .min(Integer::compareTo)
                .orElse(null);

        // No dedicated group-icon column in ui_route today — fall back to the
        // first (lowest menuOrder) child's icon as a reasonable representative.
        String groupIcon = childNodes.isEmpty() ? null : childNodes.get(0).getIcon();

        return NavigationNodeDTO.builder()
                .label(groupName)
                .path(null)
                .routeCode(null)
                .pageCode(null)
                .icon(groupIcon)
                .menuOrder(groupOrder)
                .children(childNodes)
                .build();
    }

    private NavigationNodeDTO toLeafNode(UIRoute route) {
        return NavigationNodeDTO.builder()
                .label(route.getPage() != null ? route.getPage().getPageName() : route.getRouteCode())
                .path(route.getPath())
                .routeCode(route.getRouteCode())
                .pageCode(route.getPage() != null ? route.getPage().getPageCode() : null)
                .icon(route.getIcon())
                .menuOrder(route.getMenuOrder())
                .children(List.of())
                .build();
    }

    private List<UIRoute> fetchRoutesForFacility(String facilityId) {
        if (facilityId == null || facilityId.isBlank() ) {
            return List.of();
        }

        if(FacilityId.GLOBAL.name().equals(facilityId)) {
            //later we can define global navigation template
            return List.of();
        }

        if (!facilityRouteAccessRepository.existsByFacilityId(facilityId)) {
            return List.of();
        }

        List<Long> grantedRouteIds = facilityRouteAccessRepository.findRouteIdsByFacilityId(facilityId);
        if (grantedRouteIds.isEmpty()) {
            return List.of();
        }

        return uiRouteRepository.findByIsActiveTrueAndShowInMenuTrueAndIdInOrderByMenuOrderAsc(grantedRouteIds);
    }
}
