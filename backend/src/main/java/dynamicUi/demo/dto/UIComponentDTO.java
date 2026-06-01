package dynamicUi.demo.dto;


import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UIComponentDTO {

    private Long id;

    private String pageCode;

    private String componentName;

    private String componentType;

    private String labelName;

    private String placeholder;

    private String properties;

    private Integer sequenceNo;

    private Boolean isRequired;

    private Boolean isVisible;

    private Boolean isDisabled;

    private Boolean isActive;

    private Long parentComponentId;

    private String mappingType;

    private String source;

    private String responsePath;

    private String tableName;

    private String columnName;

    private String attributeName;

    private String displayName;

    private Boolean mappingRequired;

    private Boolean isFilterable;

    private List<UILookupDTO> lookupValues;


}
