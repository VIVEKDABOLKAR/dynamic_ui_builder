package dynamicUi.demo.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ui_component")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UIComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "page_id")
    private UIPage uiPage;

    @Column(name = "component_name")
    private String componentName;

    @Column(name = "component_type")
    private String componentType;

    @Column(name = "label_name")
    private String labelName;

    @Column(name = "placeholder")
    private String placeholder;

    @Lob
    @Column(name = "properties", columnDefinition = "LONGTEXT")
    private String properties;

    @Column(name = "sequence_no")
    private Integer sequenceNo;

    @Column(name = "is_required")
    private Boolean isRequired;

    @Column(name = "is_visible")
    private Boolean isVisible;

    @Column(name = "is_disabled")
    private Boolean isDisabled;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "parent_component_id")
    private Long parentComponentId;

    @ManyToOne
    @JoinColumn(name = "lookup_master_id")
    private UILookupMaster uiLookupMaster;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (isActive == null) {
            isActive = true;
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }


}