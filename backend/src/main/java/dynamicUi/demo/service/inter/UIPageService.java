package dynamicUi.demo.service.inter;

import dynamicUi.demo.entity.UIPage;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface UIPageService {
    UIPage createPage(UIPage uiPage);
    UIPage getPageByCode(String pageCode);
    List<UIPage> getAllPages();
    UIPage updatePage(String pageCode, UIPage uiPage);
    UIPage updatePageStatus(String pageCode, boolean status);
    void deletePage(String pageCode);

    List<UIPage> getAllPages1();
}
