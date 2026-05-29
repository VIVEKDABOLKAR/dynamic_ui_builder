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

    private List<UILookupDTO> lookupValues;


}
