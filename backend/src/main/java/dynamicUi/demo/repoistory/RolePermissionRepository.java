package dynamicUi.demo.repoistory;

import dynamicUi.demo.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    List<RolePermission> findByAppRole_Code(String code);

    @Query("SELECT rp.permissionPattern FROM RolePermission rp WHERE rp.appRole.code = :code")
    List<String> findPatternsByRoleCode(@Param("code") String code);

    // Derived delete — Spring Data handles this natively (entity-by-entity
    // delete), no @Modifying/@Query needed since it's not a custom JPQL query.
    void deleteByAppRole_Id(Long roleId);
}
