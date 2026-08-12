package dynamicUi.demo.config;

import dynamicUi.demo.entity.AppRole;
import dynamicUi.demo.entity.RolePermission;
import dynamicUi.demo.repoistory.AppRoleRepository;
import dynamicUi.demo.repoistory.RolePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * One-time bootstrap for the `role` / `role_permission` tables, mirroring
 * WorkflowSeeder's approach: runs only when `role` is empty, so it's a
 * no-op on every later startup once an admin starts managing patterns.
 *
 * Seeds the two roles that currently exist in security.Role:
 *   ROLE_ADMIN  -> "*"       (everything — matches today's de-facto behavior)
 *   ROLE_VIEWER -> "*.view"  (read-only capabilities, per the proposal's
 *                             default role/pattern table)
 */
@Component
@RequiredArgsConstructor
public class RolePermissionSeeder implements CommandLineRunner {

    private final AppRoleRepository appRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (appRoleRepository.count() > 0) {
            return; // already seeded (or admin has taken over managing roles)
        }

        AppRole admin = appRoleRepository.save(
                AppRole.builder()
                        .code("ROLE_ADMIN")
                        .name("Administrator")
                        .description("Full access to all dynamic pages")
                        .build()
        );
        rolePermissionRepository.save(
                RolePermission.builder().appRole(admin).permissionPattern("*").build()
        );

        AppRole viewer = appRoleRepository.save(
                AppRole.builder()
                        .code("ROLE_VIEWER")
                        .name("Viewer")
                        .description("Read-only access to dynamic pages")
                        .build()
        );
        rolePermissionRepository.save(
                RolePermission.builder().appRole(viewer).permissionPattern("*.view").build()
        );
    }
}
