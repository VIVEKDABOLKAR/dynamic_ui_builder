package dynamicUi.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ui_component_action")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UIComponentAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The component this action belongs to
    @Column(name = "component_id", nullable = false)
    private Long componentId;

    // The page this component belongs to (for easy lookup)
    @Column(name = "page_code", nullable = false)
    private String pageCode;

    // e.g. "onClick", "onChange", "onLoad"
    @Column(name = "event", nullable = false)
    private String event;

    // References a key in the page-level "actions" map
    // e.g. "saveHome", "navHome"
    @Column(name = "action_ref", nullable = false)
    private String actionRef;

    // Optional JS-like condition, e.g. "form.isValid == true"
    // Defaults to "true" (always fire)
    @Column(name = "condition_expr")
    private String conditionExpr;

    // Display order when multiple actions fire on the same event
    @Column(name = "sequence_no")
    private Integer sequenceNo;
}