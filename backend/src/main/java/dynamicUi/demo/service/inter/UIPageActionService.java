package dynamicUi.demo.service.inter;

import dynamicUi.demo.entity.UIPageAction;

import java.util.List;

public interface UIPageActionService {

    UIPageAction create(UIPageAction uiPageAction);

    UIPageAction update(Long id, UIPageAction uiPageAction);

    UIPageAction getById(Long id);

    List<UIPageAction> getAll();

    List<UIPageAction> getByPageId(Long pageId);

    void delete(Long id);
}