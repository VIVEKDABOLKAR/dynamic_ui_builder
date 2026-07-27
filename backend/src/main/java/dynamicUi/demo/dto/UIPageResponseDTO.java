package dynamicUi.demo.dto;

import dynamicUi.demo.constant.PageStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UIPageResponseDTO {

    private Long id;

    private String pageCode;

    private String pageName;

    private String description;

    private String version;

    private String moduleCode;

    private String categoryCode;

    private String layoutCode;

    private Boolean requireAuthentication;

    private String permissionCode;

    private PageStatus status;

    private RouteResponseDTO route;
}