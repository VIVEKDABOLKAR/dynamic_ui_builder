package dynamicUi.demo.service;

import dynamicUi.demo.entity.UIPage;
import org.springframework.stereotype.Service;
import dynamicUi.demo.repoistory.UIPageRepository;
import java.util.List;

@Service
public class UIPageServiceImp implements UIPageService {

    private final UIPageRepository uiPageRepository;

    public UIPageServiceImp(UIPageRepository uiPageRepository) {
        this.uiPageRepository = uiPageRepository;
    }

    @Override
    public UIPage createPage(UIPage uiPage) {
        if (uiPageRepository.existsByPageCode(uiPage.getPageCode())) {
            throw new RuntimeException("Page code already exists");
        }
        return uiPageRepository.save(uiPage);
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
