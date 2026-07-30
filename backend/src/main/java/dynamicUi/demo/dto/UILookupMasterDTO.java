package dynamicUi.demo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UILookupMasterDTO {

    private Long id;

    private String lookupName;

    private String description;

    private Boolean isActive;

    private Long componentId;
}
