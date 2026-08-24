package dynamicUi.demo.service;

import dynamicUi.demo.dto.PageVersionDTO;
import dynamicUi.demo.entity.UIPage;
import dynamicUi.demo.entity.UIPageJson;
import dynamicUi.demo.entity.UIPageVersion;
import dynamicUi.demo.repoistory.UIPageJsonRepository;
import dynamicUi.demo.repoistory.UIPageRepository;
import dynamicUi.demo.repoistory.UIPageVersionRepository;
import dynamicUi.demo.service.inter.UIPageVersionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UIPageVersionServiceImpl implements UIPageVersionService {

    private final UIPageVersionRepository uiPageVersionRepository;
    private final UIPageJsonRepository    uiPageJsonRepository;
    private final UIPageRepository        uiPageRepository;

    public UIPageVersionServiceImpl(UIPageVersionRepository uiPageVersionRepository,
                                     UIPageJsonRepository uiPageJsonRepository,
                                     UIPageRepository uiPageRepository) {
        this.uiPageVersionRepository = uiPageVersionRepository;
        this.uiPageJsonRepository    = uiPageJsonRepository;
        this.uiPageRepository        = uiPageRepository;
    }

    @Override
    public List<PageVersionDTO> getVersionHistory(String pageCode) {
        return uiPageVersionRepository.findByUiPage_PageCodeOrderByVersionNumberDesc(pageCode)
                .stream()
                .map(PageVersionDTO::summary)
                .toList();
    }

    @Override
    @Transactional
    public PageVersionDTO createVersion(String pageCode, String changeSummary) {
        UIPage page = uiPageRepository.findByPageCode(pageCode)
                .orElseThrow(() -> new RuntimeException("Page not found: " + pageCode));

        UIPageJson currentJson = uiPageJsonRepository.findByUiPage_PageCode(pageCode)
                .orElseThrow(() -> new RuntimeException(
                        "Nothing to version yet — page has no saved JSON: " + pageCode));

        String schema = currentJson.getJsonSchema();
        if (schema == null || schema.isBlank()) {
            throw new RuntimeException("Nothing to version yet — page JSON is empty: " + pageCode);
        }

        int nextVersionNumber = uiPageVersionRepository
                .findTopByUiPage_PageCodeOrderByVersionNumberDesc(pageCode)
                .map(v -> v.getVersionNumber() + 1)
                .orElse(1);

        UIPageVersion version = UIPageVersion.builder()
                .uiPage(page)
                .versionNumber(nextVersionNumber)
                .jsonSchema(schema)
                .changeSummary(changeSummary)
                .build();

        return PageVersionDTO.summary(uiPageVersionRepository.save(version));
    }

    @Override
    public PageVersionDTO getVersion(String pageCode, Integer versionNumber) {
        UIPageVersion version = uiPageVersionRepository
                .findByUiPage_PageCodeAndVersionNumber(pageCode, versionNumber)
                .orElseThrow(() -> new RuntimeException(
                        "Version " + versionNumber + " not found for page: " + pageCode));
        return PageVersionDTO.full(version);
    }

    @Override
    @Transactional
    public void restoreVersion(String pageCode, Integer versionNumber) {
        UIPageVersion version = uiPageVersionRepository
                .findByUiPage_PageCodeAndVersionNumber(pageCode, versionNumber)
                .orElseThrow(() -> new RuntimeException(
                        "Version " + versionNumber + " not found for page: " + pageCode));

        UIPageJson currentJson = uiPageJsonRepository.findByUiPage_PageCode(pageCode)
                .orElseGet(() -> {
                    UIPage managedPage = uiPageRepository.findByPageCode(pageCode)
                            .orElseThrow(() -> new RuntimeException(
                                    "Cannot restore — page not found: " + pageCode));
                    UIPageJson newJson = new UIPageJson();
                    newJson.setUiPage(managedPage);
                    return newJson;
                });

        // Overwrites the CURRENT working state only — the version being
        // restored from remains untouched in history.
        currentJson.setJsonSchema(version.getJsonSchema());
        uiPageJsonRepository.save(currentJson);
    }
}
