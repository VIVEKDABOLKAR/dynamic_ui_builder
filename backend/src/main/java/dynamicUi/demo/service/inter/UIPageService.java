package dynamicUi.demo.service.inter;

import com.google.api.Page;
import dynamicUi.demo.constant.PageStatus;
import dynamicUi.demo.dto.UIPageRequestDTO;
import dynamicUi.demo.dto.UIPageResponseDTO;
import dynamicUi.demo.entity.UIPage;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface UIPageService {
    UIPageResponseDTO createPage(UIPageRequestDTO uiPageReq);
    UIPage getPageByCode(String pageCode);
    List<UIPage> getAllPages();
    UIPageResponseDTO updatePage(String pageCode, UIPageRequestDTO uiPage);
    UIPage updatePageStatus(String pageCode, PageStatus status);
    void deletePage(String pageCode);

    List<UIPage> getAllPages1();
}
