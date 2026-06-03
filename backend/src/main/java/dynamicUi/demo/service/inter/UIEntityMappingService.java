package dynamicUi.demo.service.inter;

import dynamicUi.demo.entity.UIEntityMapping;

import java.util.List;

public interface UIEntityMappingService {
//    List<UIEntityMapping> getLookupsByMaster(Long lookupMasterId);
    List<UIEntityMapping> getEntityMappingByComponentId(Long componentId);
}
