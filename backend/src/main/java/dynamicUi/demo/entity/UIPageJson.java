package dynamicUi.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ui_page_json")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UIPageJson {
        private static final String DEFAULT_JSON_SCHEMA = """
                        {
                              "page": {
                                  "id": 1,
                                  "pageCode": "home",
                                  "pageName": "Home Page"
                              },
                              "components": [
                                  {
                                      "id": 1,
                                      "name": "testButton",
                                      "type": "button",
                                      "sequence": 1,
                                      "properties": {
                                          "text": "Click Me",
                                          "color": "blue"
                                      }
                                  },
                                  {
                                      "id": 2,
                                      "name": "testInput",
                                      "type": "input",
                                      "sequence": 2,
                                      "properties": {
                                          "label": "input field",
                                          "placeholder": "Enter text",
                                          "width": 200
                                      }
                                  }
                              ]
                        }
                        """;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_code", referencedColumnName = "page_code", nullable = false)
    private UIPage uiPage;

    @Lob
    @Column(name = "json_schema", nullable = false, columnDefinition = "LONGTEXT")
    private String jsonSchema = "";
}
