package dynamicUi.demo.controller;

import dynamicUi.demo.entity.UIRoute;
import dynamicUi.demo.service.inter.UIRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/routes")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UIRouteController {

    private final UIRouteService service;

    @PostMapping
    public UIRoute create(@RequestBody UIRoute route) {
        return service.createRoute(route);
    }

    @GetMapping
    public List<UIRoute> getAll() {
        return service.getAllRoutes();
    }

    @GetMapping("/{routeCode}")
    public UIRoute get(@PathVariable String routeCode) {
        return service.getRoute(routeCode);
    }

    @PutMapping("/{routeCode}")
    public UIRoute update(
            @PathVariable String routeCode,
            @RequestBody UIRoute route) {

        return service.updateRoute(routeCode, route);
    }

    @DeleteMapping("/{routeCode}")
    public void delete(@PathVariable String routeCode) {

        service.deleteRoute(routeCode);
    }
}