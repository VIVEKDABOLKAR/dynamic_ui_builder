package dynamicUi.demo.repoistory;

import dynamicUi.demo.entity.UIRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface UIRouteRepository extends JpaRepository<UIRoute, Long> {

    Optional<UIRoute> findByRouteCode(String routeCode);

    Optional<UIRoute> findByPage_PageCode(String pageCode);


    boolean existsByRouteCode(String routeCode);

    boolean existsByPath(String path);

    List<UIRoute> findByIsActiveTrueOrderByMenuOrderAsc();

    Optional<UIRoute> findByPath(String path);

    List<UIRoute> findByIsActiveTrueAndShowInMenuTrueOrderByMenuOrderAsc();
}