package dynamicUi.demo.service;

import dynamicUi.demo.entity.UIComponent;
import dynamicUi.demo.entity.UILookup;
import dynamicUi.demo.entity.UIPageJson;

import java.util.List;

public interface UIPageJsonService {
    UIPageJson getByPageCode(String pageCode);

    //components to add to the ui_page_json
    void addComponentToJson(String pageCode, UIComponent component, List<UILookup> lookups);
    void updateComponentInJson(String pageCode, UIComponent component, List<UILookup> lookups);
    void removeComponentFromJson(String pageCode, Long componentId);
}