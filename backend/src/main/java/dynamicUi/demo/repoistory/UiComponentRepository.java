package dynamicUi.demo.repoistory;
import dynamicUi.demo.entity.UIComponent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UiComponentRepository extends JpaRepository<UIComponent, Long> {

    List<UIComponent> findByUiPage_PageCodeOrderBySequenceNo(String pageCode);
}
