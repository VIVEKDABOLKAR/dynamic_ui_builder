package dynamicUi.demo.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "ui_route",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "route_code"),
                @UniqueConstraint(columnNames = "path")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UIRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique Route Identifier
     * Example:
     * GATE_CHECKIN_ROUTE
     */
    @Column(name = "route_code", nullable = false, length = 100)
    private String routeCode;

    /**
     * URL Path
     * /gate/check-in
     */
    @Column(name = "path", nullable = false, length = 250)
    private String path;

    /**
     * Sidebar
     */
    @Column(name = "show_in_menu")
    private Boolean showInMenu;

    /**
     * Parent Menu
     * Gate
     */
    @Column(name = "parent_menu", length = 100)
    private String parentMenu;

    /**
     * Menu Order
     */
    @Column(name = "menu_order")
    private Integer menuOrder;

    /**
     * Breadcrumb
     */
    @Column(name = "show_breadcrumb")
    private Boolean breadcrumb;

    /**
     * Icon
     */
    @Column(name = "icon", length = 100)
    private String icon;

    /**
     * Active
     */
    @Column(name = "is_active")
    private Boolean isActive;

    // ===============================
    // One To One Mapping
    // ===============================

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "page_id",
            nullable = false,
            unique = true
    )
    private UIPage page;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

        if (isActive == null)
            isActive = true;

        if (showInMenu == null)
            showInMenu = true;

        if (breadcrumb == null)
            breadcrumb = true;

        if (menuOrder == null)
            menuOrder = 0;

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}