package dynamicUi.demo.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ui_entity_mapping")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UIEntityMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "component_id")
    private UIComponent uiComponent;

    @Column(name = "mapping_type")
    private String mappingType;

    @Column(name = "source")
    private String source;

    @Column(name = "response_path")
    private String responsePath;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "column_name")
    private String columnName;

    @Column(name = "attribute_name")
    private String attributeName;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "is_required")
    private Boolean isRequired;

    @Column(name = "is_filterable")
    private Boolean isFilterable;
}