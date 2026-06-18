package dynamicUi.demo.service.inter;

import dynamicUi.demo.entity.UIComponentAction;

import java.util.List;

public interface UIComponentActionService {

    UIComponentAction create(UIComponentAction action);

    UIComponentAction update(Long id, UIComponentAction action);

    UIComponentAction getById(Long id);

    List<UIComponentAction> getByComponentId(Long componentId);

    List<UIComponentAction> getByPageCode(String pageCode);

    void delete(Long id);

    void deleteByComponentId(Long componentId);
}