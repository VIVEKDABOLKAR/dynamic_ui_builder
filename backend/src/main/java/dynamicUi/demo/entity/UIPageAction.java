package dynamicUi.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ui_page_action")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UIPageAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "page_id")
    private UIPage uiPage;

    @Column(name = "action_name")
    private String actionName;

    @Column(name = "action_type")
    private String actionType;

    @Lob
    @Column(name = "properties", columnDefinition = "LONGTEXT")
    private String properties;
}
