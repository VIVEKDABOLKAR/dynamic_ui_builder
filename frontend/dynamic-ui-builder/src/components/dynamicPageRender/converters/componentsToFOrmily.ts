import { ComponentSchema } from "../types/JsonSchema";
import { FormilyFieldSchema } from "../types/JsonSchemaFormily";
import { convertButton } from "./rules/button.converter";
import { convertInput } from "./rules/input.converter";


export function convertComponetToField(
    component: ComponentSchema
): FormilyFieldSchema | null {

    switch (component.type) {
        case "input":
            return convertInput(component);

        case "button":
            return convertButton(component);

        default:
            console.warn(`Unsupported type: ${component.type}`);
            return null;
    }

}