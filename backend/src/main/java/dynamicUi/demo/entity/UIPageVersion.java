package dynamicUi.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Immutable snapshot of a page's JSON schema at the moment an admin
 * explicitly chose "Create Version".
 *
 * This is intentionally NOT wired into the live rendering path — the
 * runtime engine keeps reading from UIPageJson exactly as before.
 * UIPageVersion only exists for history / preview / restore.
 */
@Entity
@Table(
        name = "ui_page_version",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"page_id", "version_number"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UIPageVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", nullable = false)
    private UIPage uiPage;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Lob
    @Column(name = "json_schema", nullable = false, columnDefinition = "LONGTEXT")
    private String jsonSchema;

    @Column(name = "change_summary", length = 500)
    private String changeSummary;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
