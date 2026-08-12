package dynamicUi.demo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleCreateRequest {

    private String code;

    private String name;

    private String description;
}
