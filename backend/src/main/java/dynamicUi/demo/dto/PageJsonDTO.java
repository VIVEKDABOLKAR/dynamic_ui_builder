package dynamicUi.demo.dto;

import com.fasterxml.jackson.databind.node.ObjectNode;
import dynamicUi.demo.entity.UIPage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageJsonDTO {

    private UIPage uiPage;

    private ObjectNode jsonSchema ;
}
