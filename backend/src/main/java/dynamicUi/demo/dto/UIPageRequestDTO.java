package dynamicUi.demo.dto;

import dynamicUi.demo.constant.PageStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UIPageRequestDTO {

    private String pageCode;
    private String pageName;
    private String description;
    private String version;
    private PageStatus status;


    private String moduleCode;
    private String categoryCode;
    private String layoutCode;

    private Boolean requireAuthentication;
    private String permissionCode;


    private RouteRequestDTO route;
}