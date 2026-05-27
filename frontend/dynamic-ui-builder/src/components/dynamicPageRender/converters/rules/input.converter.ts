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
            componentId: comp.id,
            placeholder: prop.placeholder,
            width: prop.width || "100%",
            style: prop.style || {}
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