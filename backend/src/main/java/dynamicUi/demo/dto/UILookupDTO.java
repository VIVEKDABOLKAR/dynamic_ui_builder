package dynamicUi.demo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UILookupDTO {

    private Long id;
    private String lookupType;
    private String lookupValue;
    private String displayValue;
    private Integer sequenceNo;
    private Boolean isActive;
}
