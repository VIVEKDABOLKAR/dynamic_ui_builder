package dynamicUi.demo.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteRequestDTO {

    private String routeCode;
    private String path;
    private Boolean showInMenu;
    private String parentMenu;
    private Integer menuOrder;
    private Boolean breadcrumb;
    private String icon;
    private Boolean active;
}