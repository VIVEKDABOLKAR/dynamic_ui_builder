package dynamicUi.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ui_page")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UIPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "page_name", nullable = false)
    private String pageName;

    @Column(name = "page_code", nullable = false, unique = true)
    private String pageCode;

    @Column(name = "description")
    private String description;

    // FIX: field is "isActive" but old code had getter getActive() / setActive().
    // Spring Data derives the query property name from the getter, so
    // findByIsActiveTrue() resolved to property "active" — wrong.
    // Fix: getIsActive() / setIsActive() so the derived query matches.
    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (isActive == null) isActive = true;
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPageName() { return pageName; }
    public void setPageName(String pageName) { this.pageName = pageName; }

    public String getPageCode() { return pageCode; }
    public void setPageCode(String pageCode) { this.pageCode = pageCode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // FIXED getters/setters — match Spring Data naming convention
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    // Kept as deprecated aliases so callers in UIPageServiceImp don't break immediately
    // TODO: migrate all callers to getIsActive() and remove these
    @Deprecated
    public Boolean getActive() { return isActive; }
    @Deprecated
    public void setActive(Boolean active) { this.isActive = active; }
}