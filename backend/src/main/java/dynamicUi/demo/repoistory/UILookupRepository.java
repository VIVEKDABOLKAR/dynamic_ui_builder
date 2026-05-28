package dynamicUi.demo.repoistory;

import dynamicUi.demo.entity.UILookup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UILookupRepository extends JpaRepository<UILookup, Long> {

    List<UILookup> findByUiLookupMaster_Id(Long lookupMasterId);

    void deleteByUiLookupMaster_Id(Long lookupMasterId);
}
