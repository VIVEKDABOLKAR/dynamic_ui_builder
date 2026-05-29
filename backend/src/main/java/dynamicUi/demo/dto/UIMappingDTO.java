package dynamicUi.demo.dto;

import dynamicUi.demo.entity.UIComponent;
import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UIMappingDTO {

    private Long id;
    private UIComponent uiComponent;
    private String tableName;
    private String columnName;
    private String attributeName;
    private String displayName;
    private Boolean isRequired;
    private Boolean isFilterable;
}
