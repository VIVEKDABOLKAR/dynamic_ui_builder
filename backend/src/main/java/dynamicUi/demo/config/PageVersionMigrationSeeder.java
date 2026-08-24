package dynamicUi.demo.config;

import dynamicUi.demo.entity.UIPage;
import dynamicUi.demo.entity.UIPageJson;
import dynamicUi.demo.entity.UIPageVersion;
import dynamicUi.demo.repoistory.UIPageJsonRepository;
import dynamicUi.demo.repoistory.UIPageRepository;
import dynamicUi.demo.repoistory.UIPageVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * One-time migration for page versioning: any existing page that already
 * has a saved JSON schema but no version history yet gets a "Version 1"
 * snapshot created from its current JSON, so version history never starts
 * empty for pages that predate this feature.
 *
 * Safe to run on every startup — pages that already have at least one
 * version are skipped, so this becomes a no-op once migrated.
 */
@Component
@RequiredArgsConstructor
public class PageVersionMigrationSeeder implements CommandLineRunner {

    private final UIPageRepository        uiPageRepository;
    private final UIPageJsonRepository    uiPageJsonRepository;
    private final UIPageVersionRepository uiPageVersionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        for (UIPage page : uiPageRepository.findAll()) {
            if (uiPageVersionRepository.existsByUiPage_PageCode(page.getPageCode())) {
                continue; // already has version history
            }

            UIPageJson pageJson = uiPageJsonRepository
                    .findByUiPage_PageCode(page.getPageCode())
                    .orElse(null);

            if (pageJson == null || pageJson.getJsonSchema() == null || pageJson.getJsonSchema().isBlank()) {
                continue; // nothing to snapshot yet
            }

            uiPageVersionRepository.save(
                    UIPageVersion.builder()
                            .uiPage(page)
                            .versionNumber(1)
                            .jsonSchema(pageJson.getJsonSchema())
                            .changeSummary("Migrated from existing page configuration")
                            .build()
            );
        }
    }
}
