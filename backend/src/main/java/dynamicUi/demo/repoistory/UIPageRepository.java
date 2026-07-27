package dynamicUi.demo.repoistory;

import java.util.List;
import java.util.Optional;

import dynamicUi.demo.constant.PageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dynamicUi.demo.entity.UIPage;

@Repository
public interface UIPageRepository  extends JpaRepository<UIPage, Long> {
    boolean existsByPageCode(String pageCode);

    Optional<UIPage> findByPageCode(String pageCode);

    List<UIPage> findAll();

    boolean existsByRoute_Path(String path);


    List<UIPage> findByStatus(PageStatus status);

    List<UIPage> findByStatusNot(PageStatus status);

    List<UIPage> findByModuleCode(String moduleCode);
}
