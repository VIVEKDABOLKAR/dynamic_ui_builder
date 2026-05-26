import { ComponentSchema } from "../../types/JsonSchema";
import { FormilyFieldSchema } from "../../types/JsonSchemaFormily";


export function convertButton(
  component: ComponentSchema
): FormilyFieldSchema {
  const p = component.properties;

  return {
    type: "void",
    "x-component": "CustomButton",
    "x-component-props": {
      text: p.text,
      variant: p.variant
    },
    "x-index": component.sequence
  };
}