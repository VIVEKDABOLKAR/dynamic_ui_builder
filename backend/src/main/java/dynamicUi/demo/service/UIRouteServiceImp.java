package dynamicUi.demo.service;

import dynamicUi.demo.dto.RouteResponseDTO;
import dynamicUi.demo.entity.UIRoute;
import dynamicUi.demo.mapper.UIRouteMapper;
import dynamicUi.demo.repoistory.FacilityRouteAccessRepository;
import dynamicUi.demo.repoistory.UIRouteRepository;
import dynamicUi.demo.service.inter.UIRouteService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UIRouteServiceImp implements UIRouteService {

    private final UIRouteRepository repository;
    private final UIRouteMapper uiRouteMapper;
    private final FacilityRouteAccessRepository facilityRouteAccessRepository;

    @Override
    public RouteResponseDTO resolveByPath(String path) {
        //get route object
        UIRoute route = repository.findByPath(path)
                .orElseThrow(() -> new RuntimeException(
                        "ROUTE NOT FOUND :: path = " + path));

        if (!Boolean.TRUE.equals(route.getIsActive()))
            throw new RuntimeException("ROUTE NOT ACTIVE :: path = " + path);

        return uiRouteMapper.toResponse(route);
    }

    @Override
    public RouteResponseDTO resolveByPathAndFacility(String path, String selectedFacilityId) {

        //get route object
        UIRoute route = repository.findByPath(path)
                .orElseThrow(() -> new RuntimeException(
                        "ROUTE NOT FOUND :: path = " + path));

        //validate selectedFacilityId and RouteId is exists
        if(!facilityRouteAccessRepository.existsByFacilityIdAndRouteIdAndActiveTrue(selectedFacilityId, route.getId())) {
            //throws forbidden exception
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not have access to this route."
            );
        }

        if (!Boolean.TRUE.equals(route.getIsActive()))
            throw new RuntimeException("ROUTE NOT ACTIVE :: path = " + path);

        return uiRouteMapper.toResponse(route);
    }


    @Override
    public UIRoute createRoute(UIRoute route) {

        if (repository.existsByRouteCode(route.getRouteCode()))
            throw new RuntimeException("Route code already exists.");

        if (repository.existsByPath(route.getPath()))
            throw new RuntimeException("Route path already exists.");

        return repository.save(route);
    }

    @Override
    public UIRoute updateRoute(String routeCode, UIRoute request) {

        UIRoute route = repository.findByRouteCode(routeCode)
                .orElseThrow(() -> new RuntimeException("Route not found"));

        route.setPath(request.getPath());
        route.setShowInMenu(request.getShowInMenu());
        route.setParentMenu(request.getParentMenu());
        route.setMenuOrder(request.getMenuOrder());
        route.setBreadcrumb(request.getBreadcrumb());
        route.setIcon(request.getIcon());
        route.setIsActive(request.getIsActive());

        return repository.save(route);
    }

    @Override
    public UIRoute getRoute(String routeCode) {

        return repository.findByRouteCode(routeCode)
                .orElseThrow(() -> new RuntimeException("Route not found"));
    }

    @Override
    public List<UIRoute> getAllRoutes() {

        return repository.findByIsActiveTrueOrderByMenuOrderAsc();
    }

    @Override
    public void deleteRoute(String routeCode) {

        UIRoute route = repository.findByRouteCode(routeCode)
                .orElseThrow(() -> new RuntimeException("Route not found"));

        route.setIsActive(false);

        repository.save(route);
    }
}