package dynamicUi.demo.repoistory;

import dynamicUi.demo.entity.UIPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UIPageRepository  extends JpaRepository<UIPage, Long> {
    boolean existsByPageCode(String pageCode);
    Optional<UIPage> findByPageCode(String pageCode);
    List<UIPage> findAll();
}
