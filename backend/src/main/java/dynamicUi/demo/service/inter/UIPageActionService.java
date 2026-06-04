package dynamicUi.demo.service.inter;

import java.util.List;

import dynamicUi.demo.dto.ComponentActionRequest;
import dynamicUi.demo.entity.UIPageAction;

public interface UIPageActionService {

    UIPageAction create(String pageCode, UIPageAction uiPageAction);

    UIPageAction update(Long id, UIPageAction uiPageAction);

    UIPageAction getById(Long id);

    List<UIPageAction> getByPageCode(String pageCode);

    void delete(Long id);


    void addComponentAction(
            String pageCode,
            ComponentActionRequest request);
}
