package dynamicUi.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UIEntityMappingDTO {

    private String tableName;

    private String columnName;

    private String attributeName;

    private String displayName;

    private Boolean isRequired;

    private Boolean isFilterable;
}
