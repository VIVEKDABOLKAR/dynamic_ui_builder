package dynamicUi.demo.repoistory;

import dynamicUi.demo.entity.UIComponentAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UIComponentActionRepository extends JpaRepository<UIComponentAction, Long> {

    // All actions for a specific component (ordered by sequence)
    List<UIComponentAction> findByComponentIdOrderBySequenceNoAsc(Long componentId);

    // All component actions for a page (used at assembly time)
    List<UIComponentAction> findByPageCodeOrderByComponentIdAscSequenceNoAsc(String pageCode);

    // All actions for a specific component + event (e.g. all "onClick" actions)
    List<UIComponentAction> findByComponentIdAndEventOrderBySequenceNoAsc(Long componentId, String event);

    // Delete all actions for a component (used when component is deleted)
    void deleteByComponentId(Long componentId);
}
