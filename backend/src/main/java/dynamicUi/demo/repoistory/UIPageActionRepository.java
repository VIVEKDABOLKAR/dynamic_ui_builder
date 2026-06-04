package dynamicUi.demo.repoistory;


import dynamicUi.demo.entity.UIPageAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UIPageActionRepository extends JpaRepository<UIPageAction, Long> {

//    List<UIPageAction> findByUiPageId(Long pageId);

List<UIPageAction> findByUiPagecode(String pageCode);
}
