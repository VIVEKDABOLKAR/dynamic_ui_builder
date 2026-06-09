import { ComponentSchema } from "../types/JsonSchema";
import { ActionRegistry } from "../types/JsonSchema";
import { FormilyFieldSchema } from "../types/JsonSchemaFormily";
import { convertButton } from "./rules/button.converter";
import { convertCard } from "./rules/card.converter";
import { convertCheckbox } from "./rules/checkbox.converter";
import { convertDatePicker } from "./rules/datepicker.converter";
import { convertHeading } from "./rules/heading.converter";
import { convertInput } from "./rules/input.converter";
import { convertLayout } from "./rules/layout.converter";
import { convertRadio } from "./rules/radio.converter";
import { convertSelect } from "./rules/select.converter";
import { convertTable } from "./rules/table.converter";
import { convertTextarea } from "./rules/textarea.converter";


export function convertComponetToField(
    component: ComponentSchema,
    actionRegistry: ActionRegistry = {}
): FormilyFieldSchema | null {

    switch (component.type) {
        case "input":
            return convertInput(component, actionRegistry);

        case "button":
            return convertButton(component, actionRegistry);

        case "heading":
            return convertHeading(component, actionRegistry);

        case "layout":
            return convertLayout(component, actionRegistry);

        case "card":
            return convertCard(component, actionRegistry);

        case "textarea":
            return convertTextarea(component, actionRegistry);

        case "select":
            return convertSelect(component, actionRegistry);

        case "checkbox":
            return convertCheckbox(component, actionRegistry);

        case "radio":
            return convertRadio(component, actionRegistry);

        case "table":
            return convertTable(component, actionRegistry);

        case "datepicker":
            return convertDatePicker(component, actionRegistry);

        default:
            console.warn(`Unsupported type: ${component.type}`);
            return null;
    }

}