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

    // FIX 1: Removed DEFAULT_JSON_SCHEMA constant — it was defined but never
    //         referenced anywhere in the codebase (dead code).
    //
    // FIX 2: Field initialiser  jsonSchema = ""  is bypassed by both Lombok's
    //         @AllArgsConstructor and JPA's no-arg constructor, so jsonSchema
    //         could arrive as null and cause NPE in OBJECT_MAPPER.readTree().
    //         Fix: use @PrePersist to guarantee a non-null default before DB insert.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_code", referencedColumnName = "page_code", nullable = false)
    private UIPage uiPage;

    @Lob
    @Column(name = "json_schema", nullable = false, columnDefinition = "LONGTEXT")
    private String jsonSchema;

    @PrePersist
    public void prePersist() {
        if (jsonSchema == null) {
            jsonSchema = "{}";
        }
    }
}