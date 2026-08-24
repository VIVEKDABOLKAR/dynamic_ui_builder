package dynamicUi.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePageVersionRequest {
    // Optional short note describing what changed — purely informational.
    private String changeSummary;
}
