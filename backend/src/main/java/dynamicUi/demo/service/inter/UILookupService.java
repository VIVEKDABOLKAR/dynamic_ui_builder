package dynamicUi.demo.service.inter;

import dynamicUi.demo.dto.UILookupDTO;

import java.util.List;

public interface UILookupService {
    List<UILookupDTO> getLookupsByMaster(Long lookupMasterId);
    List<UILookupDTO> getLookupsByComponent(Long componentId);
    List<UILookupDTO> getLookupsByType(String lookupType);

    List<UILookupDTO> getAllLookups();
    UILookupDTO getLookupById(Long id);
    UILookupDTO createLookup(UILookupDTO dto);
    UILookupDTO updateLookup(Long id, UILookupDTO dto);
    void deleteLookup(Long id);

}
