package dynamicUi.demo.entity;

import dynamicUi.demo.constant.PageStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "ui_page",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "page_code")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UIPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//     ==========================================
//     Basic Information
//     ==========================================

    @Column(name = "page_code", nullable = false, length = 100)
    private String pageCode;

    @Column(name = "page_name", nullable = false, length = 150)
    private String pageName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "version", length = 20)
    private String version;


    // ==========================================
    // Registry References
    // (Store codes only)
    // ==========================================

    @Column(name = "module_code", length = 50)
    private String moduleCode;

    @Column(name = "category_code", length = 50)
    private String categoryCode;

    @Column(name = "layout_code", length = 50)
    private String layoutCode;

    // ==========================================
    // Security
    // ==========================================

    @Column(name = "require_authentication")
    private Boolean requireAuthentication;

    @Column(name = "permission_code", length = 100)
    private String permissionCode;

    // ==========================================
    // Route :- UIRoute
    // ==========================================

    @OneToOne(
            mappedBy = "page",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private UIRoute route;

    public void setRoute(UIRoute route) {
        this.route = route;

        if (route != null) {
            route.setPage(this);
        }
    }

    // ==========================================
    // Status
    // ==========================================

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PageStatus status;

    // ==========================================
    // Audit
    // ==========================================

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

        if (status == null) {
            status = PageStatus.DRAFT;
        }

        if (requireAuthentication == null) {
            requireAuthentication = true;
        }


        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return status == PageStatus.ACTIVE;
    }
}