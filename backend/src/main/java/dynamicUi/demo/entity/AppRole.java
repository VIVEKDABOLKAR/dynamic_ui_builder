package dynamicUi.demo.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * DB-driven counterpart to {@code dynamicUi.demo.security.Role}.
 *
 * The security.Role enum still decides what a user can log in as (JWT
 * "role" claim, Spring authorities) — that part is untouched. This table
 * only holds the *permission patterns* for each role code, so admins can
 * add/edit patterns like "gate.*" without a redeploy.
 *
 * {@code code} must match a security.Role enum name (e.g. "ROLE_ADMIN")
 * for the pattern lookup in AuthorizationService to find it. Adding a
 * brand-new role code here does NOT make it assignable to a user — that
 * still requires adding the value to the security.Role enum, since
 * AppUser.role is a typed enum column.
 */
@Entity
@Table(
        name = "role",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "code")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 255)
    private String description;
}
