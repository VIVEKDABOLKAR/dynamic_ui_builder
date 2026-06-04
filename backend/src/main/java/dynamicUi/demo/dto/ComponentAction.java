package dynamicUi.demo.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComponentAction {

    private String event;

    private String ref;

    private String condition;
}