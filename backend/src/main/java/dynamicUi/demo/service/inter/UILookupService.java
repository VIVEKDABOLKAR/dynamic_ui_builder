package dynamicUi.demo.service.inter;

import dynamicUi.demo.dto.UILookupDTO;

import java.util.List;

public interface UILookupService {
    List<UILookupDTO> getLookupsByMaster(Long lookupMasterId);
    List<UILookupDTO> getLookupsByComponent(Long componentId);
}
