package dynamicUi.demo.service.inter;


import dynamicUi.demo.dto.UIComponentDTO;
import dynamicUi.demo.dto.UIEntityMappingDTO;

import java.util.List;

public interface UiComponentService {

    UIComponentDTO createComponent(UIComponentDTO componentDTO, UIEntityMappingDTO mappingDTO);

    List<UIComponentDTO> getComponentsByPage(String pageCode);

    UIComponentDTO getComponentById(Long id);

    UIComponentDTO updateComponent(Long id, UIComponentDTO componentDTO, UIEntityMappingDTO mappingDTO);

    void deleteComponent(Long id);
}
