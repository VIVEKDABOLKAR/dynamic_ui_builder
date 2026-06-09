package dynamicUi.demo.service.inter;

import dynamicUi.demo.entity.UIPage;
import dynamicUi.demo.entity.UIPageJson;

import java.util.List;

public interface UIPageJsonService {
    UIPageJson getByPageCode(String pageCode);

    void syncPageJson(String pageCode);

    List<UIPage> getAllActivePages();
}