package dynamicUi.demo.service.inter;

import dynamicUi.demo.entity.UIRoute;

import java.util.List;

public interface UIRouteService {

    UIRoute createRoute(UIRoute route);

    UIRoute updateRoute(String routeCode, UIRoute route);

    UIRoute getRoute(String routeCode);

    List<UIRoute> getAllRoutes();

    void deleteRoute(String routeCode);
}