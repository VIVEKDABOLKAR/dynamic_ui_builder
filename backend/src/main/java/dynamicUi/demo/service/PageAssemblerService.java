package dynamicUi.demo.service;

import dynamicUi.demo.constant.PageStatus;
import dynamicUi.demo.dto.PageJsonDTO;
import dynamicUi.demo.entity.UIPage;
import dynamicUi.demo.repoistory.UIPageRepository;
import dynamicUi.demo.service.inter.AuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PageAssemblerService {

    private final UIPageRepository uiPageRepository;
    private final AuthorizationService authorizationService;
    private final PageConfigurationCacheService pageConfigurationCacheService;

    /**
     * Gets the complete page JSON for the given pageCode.
     *
     * Authorization is ALWAYS checked here.
     *
     * The actual page configuration is retrieved from
     * PageConfigurationCacheService.
     */
    public PageJsonDTO getPageAssembledByPageCode(String pageCode) {

        // ---------------------------------------------------------
        // 1. Find page
        // ---------------------------------------------------------
        UIPage uiPage = uiPageRepository.findByPageCode(pageCode)
                .orElseThrow(() -> new RuntimeException(
                        "PAGE NOT FOUND :: pageCode = " + pageCode
                ));

        // ---------------------------------------------------------
        // 2. Validate page status
        // ---------------------------------------------------------
        if (!(uiPage.getStatus() != PageStatus.INACTIVE
                && uiPage.getStatus() != PageStatus.DRAFT)) {

            throw new RuntimeException(
                    "Page is not active :: pageCode = " + pageCode
            );
        }

        // ---------------------------------------------------------
        // 3. Authorization MUST happen on every request
        // ---------------------------------------------------------
        authorizationService.requirePagePermission(uiPage);

        // ---------------------------------------------------------
        // 4. Get page configuration from cache
        // ---------------------------------------------------------
        return pageConfigurationCacheService
                .getPageConfiguration(pageCode);
    }
}