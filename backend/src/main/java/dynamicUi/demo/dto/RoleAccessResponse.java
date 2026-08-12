package dynamicUi.demo.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleAccessResponse {

    private String code;

    private String name;

    private List<String> patterns;
}
