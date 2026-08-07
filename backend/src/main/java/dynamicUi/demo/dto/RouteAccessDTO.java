package dynamicUi.demo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteAccessDTO {
    private Long routeId;
    private String routeCode;
    private String pageName; // from the linked UIPage, may be null
    private String path;
    private boolean granted;
}
