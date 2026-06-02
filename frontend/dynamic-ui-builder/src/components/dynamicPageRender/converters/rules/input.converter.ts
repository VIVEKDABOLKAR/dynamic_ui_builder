import { ActionRegistry, ComponentSchema } from "../../types/JsonSchema";
import { FormilyFieldSchema } from "../../types/JsonSchemaFormily";
import { convertDefaultFieldSchema } from "./default/default.converter";

export function convertInput(
    comp: ComponentSchema,
    actionRegistry: ActionRegistry = {}
): FormilyFieldSchema {

    const prop = comp.properties;
    const base = convertDefaultFieldSchema(comp, actionRegistry);

    return {
        ...base,
        type: "string",
        title: prop.label,
        "x-component": "Input",
        "x-decorator": "form",

        "x-component-props": {
            ...base["x-component-props"],
            placeholder: prop.placeholder,
            width: prop.width || "100%",
            style: prop.style || {},
        },

        ...(prop.required && {
            "x-validator": [
                {
                    required: true,
                    message: `${prop.label} is required`
                }
            ]
        })
    };
}