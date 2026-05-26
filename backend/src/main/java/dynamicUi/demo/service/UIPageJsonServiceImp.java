package dynamicUi.demo.service;

import org.springframework.stereotype.Service;

import dynamicUi.demo.entity.UIPage;
import dynamicUi.demo.entity.UIPageJson;
import dynamicUi.demo.repoistory.UIPageJsonRepository;

@Service
public class UIPageJsonServiceImp implements UIPageJsonService {

    private final UIPageJsonRepository uiPageJsonRepository;

    public UIPageJsonServiceImp(UIPageJsonRepository uiPageJsonRepository) {
        this.uiPageJsonRepository = uiPageJsonRepository;
    }

    @Override
    public UIPageJson getByPageCode(String pageCode) {
        return uiPageJsonRepository.findByUiPage_PageCode(pageCode)
            .orElseGet(() -> {
                UIPageJson j = new UIPageJson();
                UIPage p = new UIPage();
                p.setPageCode(pageCode);
                j.setUiPage(p);
                return j;
            });
    }
}