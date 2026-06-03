package dynamicUi.demo.entity;

import com.fasterxml.jackson.databind.JsonNode;
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

    @Column(name = "page_code")
    private String uiPagecode;

    @Column(name = "action_name")
    private String actionName;

    @Column(name = "action_type")
    private String actionType;

    @Lob
    @Column(name = "properties", columnDefinition = "LONGTEXT")
    private String properties;
}
