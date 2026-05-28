import { ComponentSchema } from "../types/JsonSchema";
import { FormilyFieldSchema } from "../types/JsonSchemaFormily";
import { convertButton } from "./rules/button.converter";
import { convertCard } from "./rules/card.converter";
import { convertCheckbox } from "./rules/checkbox.converter";
import { convertHeading } from "./rules/heading.converter";
import { convertInput } from "./rules/input.converter";
import { convertRadio } from "./rules/radio.converter";
import { convertSelect } from "./rules/select.converter";
import { convertTable } from "./rules/table.converter";
import { convertTextarea } from "./rules/textarea.converter";


export function convertComponetToField(
    component: ComponentSchema
): FormilyFieldSchema | null {

    switch (component.type) {
        case "input":
            return convertInput(component);

        case "button":
            return convertButton(component);

        case "heading":
            return convertHeading(component);

        case "card":
            return convertCard(component);

        case "textarea":
            return convertTextarea(component);

        case "select":
            let response;
            convertSelect(component).then((res) => {
                response = res;
            });
            return response || null;

        case "checkbox":
            return convertCheckbox(component);

        case "radio":
            return convertRadio(component);

        case "table":
            return convertTable(component);

        default:
            console.warn(`Unsupported type: ${component.type}`);
            return null;
    }

}