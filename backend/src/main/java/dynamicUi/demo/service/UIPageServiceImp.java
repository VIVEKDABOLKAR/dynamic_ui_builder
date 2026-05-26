package dynamicUi.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dynamicUi.demo.entity.UIPage;
import dynamicUi.demo.entity.UIPageJson;
import dynamicUi.demo.repoistory.UIPageJsonRepository;
import dynamicUi.demo.repoistory.UIPageRepository;

@Service
public class UIPageServiceImp implements UIPageService {

    private final UIPageRepository uiPageRepository;
    private final UIPageJsonRepository uiPageJsonRepository;

    public UIPageServiceImp(UIPageRepository uiPageRepository, UIPageJsonRepository uiPageJsonRepository) {
        this.uiPageRepository = uiPageRepository;
        this.uiPageJsonRepository = uiPageJsonRepository;
    }

    @Override
    public UIPage createPage(UIPage uiPage) {
        if (uiPageRepository.existsByPageCode(uiPage.getPageCode())) {
            throw new RuntimeException("Page code already exists");
        }
        UIPage uiPageSaved = uiPageRepository.save(uiPage);
        UIPageJson uiPageJson = new UIPageJson();
        uiPageJson.setUiPage(uiPageSaved);
        uiPageJsonRepository.save(uiPageJson);
        return uiPageSaved;
    }

    @Override
    public UIPage getPageByCode(String pageCode) {
        return uiPageRepository.findByPageCode(pageCode)
                .orElseThrow(() -> new RuntimeException("Page not found with code: " + pageCode));
    }

    @Override
    public List<UIPage> getAllPages() {
        return uiPageRepository.findAll();
    }

    @Override
    public UIPage updatePage(String pageCode, UIPage uiPage) {
        UIPage existingPage = uiPageRepository.findByPageCode(pageCode)
                .orElseThrow(() -> new RuntimeException("Page not found with code: " + pageCode));

        if (uiPage.getPageName() != null) {
            existingPage.setPageName(uiPage.getPageName());
        }
        if (uiPage.getDescription() != null) {
            existingPage.setDescription(uiPage.getDescription());
        }
        if (uiPage.getActive() != null) {
            existingPage.setActive(uiPage.getActive());
        }

        return uiPageRepository.save(existingPage);
    }

    @Override
    public void deletePage(String pageCode) {
        UIPage page = uiPageRepository.findByPageCode(pageCode)
                .orElseThrow(() -> new RuntimeException("Page not found with code: " + pageCode));
        page.setActive(false);
        uiPageRepository.save(page);
    }
}
