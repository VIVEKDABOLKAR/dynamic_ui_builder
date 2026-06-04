package dynamicUi.demo.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComponentActionRequest {

    private Long componentId;

    private String event;

    private String ref;

    private String condition;

    private String uiPagecode;
}