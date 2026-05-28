package dynamicUi.demo.service;


import dynamicUi.demo.dto.UIComponentDTO;

import java.util.List;

public interface UiComponentService {

    UIComponentDTO createComponent(UIComponentDTO dto);

    List<UIComponentDTO> getComponentsByPage(String pageCode);

    UIComponentDTO getComponentById(Long id);

    UIComponentDTO updateComponent(Long id, UIComponentDTO dto);

    void deleteComponent(Long id);
}
