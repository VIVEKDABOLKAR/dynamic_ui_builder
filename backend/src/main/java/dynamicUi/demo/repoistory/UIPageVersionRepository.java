package dynamicUi.demo.repoistory;

import dynamicUi.demo.entity.UIPageVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UIPageVersionRepository extends JpaRepository<UIPageVersion, Long> {

    List<UIPageVersion> findByUiPage_PageCodeOrderByVersionNumberDesc(String pageCode);

    Optional<UIPageVersion> findByUiPage_PageCodeAndVersionNumber(String pageCode, Integer versionNumber);

    Optional<UIPageVersion> findTopByUiPage_PageCodeOrderByVersionNumberDesc(String pageCode);

    boolean existsByUiPage_PageCode(String pageCode);
}
