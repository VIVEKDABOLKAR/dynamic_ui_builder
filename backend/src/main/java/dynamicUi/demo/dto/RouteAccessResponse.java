package dynamicUi.demo.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteAccessResponse {
    private String facilityId; // null when this is the Global view
    private boolean global;
    private List<RouteAccessDTO> routes;
}
