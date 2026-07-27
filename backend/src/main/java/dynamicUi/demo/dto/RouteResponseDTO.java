package dynamicUi.demo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteResponseDTO {

    private Long id;

    private String routeCode;

    private String path;

    private Boolean showInMenu;

    private String parentMenu;

    private Integer menuOrder;

    private Boolean breadcrumb;

    private String icon;

    private Boolean active;

    /**
     * Denormalized from UIRoute.page.pageCode so the frontend can
     * go straight from a resolved route to fetching /api/ui/pages/{pageCode}
     * without a second lookup.
     */
    private String pageCode;

    private String pageName;
}