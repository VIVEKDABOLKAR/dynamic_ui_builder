import { ComponentSchema } from "../../types/JsonSchema";
import { FormilyFieldSchema } from "../../types/JsonSchemaFormily";

export function convertInput(
    comp: ComponentSchema
): FormilyFieldSchema {

    const prop = comp.properties;

    return {
        type: "string",
        title: prop.label,
        "x-component": "Input",
        "x-decorator": "form",

        "x-component-props": {
            placeholder: prop.placeholder,
            style: {
                width: prop.width
            }
        },

        "x-visible": prop.visible ?? true,
        "x-disabled": prop.disabled ?? false,
        "x-index": comp.sequence,

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