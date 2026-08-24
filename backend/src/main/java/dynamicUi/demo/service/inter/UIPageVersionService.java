package dynamicUi.demo.service.inter;

import dynamicUi.demo.dto.PageVersionDTO;

import java.util.List;

public interface UIPageVersionService {

    /** Version history for a page, newest first. Lightweight — no jsonSchema. */
    List<PageVersionDTO> getVersionHistory(String pageCode);

    /** Snapshot the page's CURRENT working JSON (UIPageJson) as a new immutable version. */
    PageVersionDTO createVersion(String pageCode, String changeSummary);

    /** Single version, including its full jsonSchema — used for preview. */
    PageVersionDTO getVersion(String pageCode, Integer versionNumber);

    /**
     * Loads a past version's JSON back into the page's current working state
     * (UIPageJson). Does NOT create a new version entry — the admin can
     * review/edit the restored content and click "Create Version" if they
     * want it to become permanent history.
     */
    void restoreVersion(String pageCode, Integer versionNumber);
}
