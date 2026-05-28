package dynamicUi.demo.repoistory;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dynamicUi.demo.entity.UIComponent;

public interface UiComponentRepository extends JpaRepository<UIComponent, Long> {

    List<UIComponent> findByUiPage_PageCodeAndIsActiveTrueOrderBySequenceNo(String pageCode);
}
