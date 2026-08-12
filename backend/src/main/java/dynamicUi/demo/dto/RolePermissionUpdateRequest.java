package dynamicUi.demo.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionUpdateRequest {

    private List<String> patterns;
}
