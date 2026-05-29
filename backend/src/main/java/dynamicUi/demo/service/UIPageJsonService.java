package dynamicUi.demo.service;

import dynamicUi.demo.entity.UIPageJson;

public interface UIPageJsonService {
    UIPageJson getByPageCode(String pageCode);

    void syncPageJson(String pageCode);
}