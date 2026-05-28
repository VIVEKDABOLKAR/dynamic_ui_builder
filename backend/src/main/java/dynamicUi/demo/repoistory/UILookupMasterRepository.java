package dynamicUi.demo.repoistory;

import dynamicUi.demo.entity.UILookupMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UILookupMasterRepository extends JpaRepository<UILookupMaster, Long> {
	java.util.Optional<UILookupMaster> findByComponentId(Long componentId);
}
