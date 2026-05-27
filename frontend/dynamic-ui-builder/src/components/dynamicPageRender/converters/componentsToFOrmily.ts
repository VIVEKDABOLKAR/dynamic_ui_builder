import { ComponentSchema } from "../types/JsonSchema";
import { FormilyFieldSchema } from "../types/JsonSchemaFormily";
import { convertButton } from "./rules/button.converter";
import { convertCard } from "./rules/card.converter";
import { convertCheckbox } from "./rules/checkbox.converter";
import { convertHeading } from "./rules/heading.converter";
import { convertInput } from "./rules/input.converter";
import { convertSelect } from "./rules/select.converter";
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
            return convertSelect(component);

        case "checkbox":
            return convertCheckbox(component);

        default:
            console.warn(`Unsupported type: ${component.type}`);
            return null;
    }

}