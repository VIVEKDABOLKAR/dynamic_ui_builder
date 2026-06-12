package dynamicUi.demo.demo;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PageUpdateMessage {
    private String pageCode;
    private String pageName;
    private String action;        // "UPDATED", "CREATED", "DELETED"
    private LocalDateTime updatedAt;
}