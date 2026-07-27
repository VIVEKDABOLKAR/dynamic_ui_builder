package dynamicUi.demo.dto;

import lombok.*;

import java.util.List;

/**
 * One node in the sidebar tree built from UIRoute rows.
 *
 * Two shapes:
 *  - Group node   : path == null, children populated (e.g. "Gate")
 *  - Leaf node     : path != null, children empty      (e.g. "Check In")
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NavigationNodeDTO {

    /** Display label — parentMenu for a group, pageName for a leaf. */
    private String label;

    /** Null for group nodes. */
    private String path;

    /** Null for group nodes. */
    private String routeCode;

    /** Null for group nodes. */
    private String pageCode;

    private String icon;

    private Integer menuOrder;

    @Builder.Default
    private List<NavigationNodeDTO> children = List.of();
}
