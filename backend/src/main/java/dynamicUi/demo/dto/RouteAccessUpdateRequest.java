package dynamicUi.demo.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteAccessUpdateRequest {
    private List<Long> routeIds; // the complete set of routes that should be granted
}
