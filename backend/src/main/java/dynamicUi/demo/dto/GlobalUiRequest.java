package dynamicUi.demo.dto;

import tools.jackson.databind.JsonNode;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GlobalUiRequest {

    private String facilityId;
    private String facilityName;
    private String type;
    private JsonNode config;

}