package dynamicUi.demo.repoistory;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dynamicUi.demo.entity.UIPageJson;

@Repository
public interface UIPageJsonRepository extends JpaRepository<UIPageJson, Long> {
    // get json by referenced ui page's pageCode
    Optional<UIPageJson> findByUiPage_PageCode(String pageCode);

    // backward-compatible method name (optional)
    default Optional<UIPageJson> findByPageCode(String pageCode) {
        return findByUiPage_PageCode(pageCode);
    }
}
