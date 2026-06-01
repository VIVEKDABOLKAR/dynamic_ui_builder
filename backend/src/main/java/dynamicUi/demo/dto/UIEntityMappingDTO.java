package dynamicUi.demo.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UIEntityMappingDTO {

    private String mappingType;

    private String source;

    private String responsePath;

    private String tableName;

    private String columnName;

    private String attributeName;

    private String displayName;

    private Boolean isRequired;

    private Boolean isFilterable;
}
