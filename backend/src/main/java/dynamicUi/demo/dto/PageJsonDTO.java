package dynamicUi.demo.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dynamicUi.demo.entity.UIPage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageJsonDTO {

    private UIPage uiPage;

//     FIX: Jackson was serializing ObjectNode's internal metadata fields
//     (array, boolean, nodeType, etc.) instead of the actual JSON content.
//     @JsonRawValue tells Jackson to write this String as raw JSON directly
//     into the response — no escaping, no wrapping.
    @JsonRawValue
    private String jsonSchema;

    // Convenience setter that accepts ObjectNode and converts to String
    public void setJsonSchema(ObjectNode node) {
        this.jsonSchema = node != null ? node.toString() : null;
    }

    // Keep a String setter as well for direct String assignment
    public void setJsonSchema(String json) {
        this.jsonSchema = json;
    }
}