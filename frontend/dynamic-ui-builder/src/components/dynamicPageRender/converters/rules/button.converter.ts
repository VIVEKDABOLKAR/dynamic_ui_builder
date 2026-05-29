import { ComponentSchema } from "../../types/JsonSchema";
import { FormilyFieldSchema } from "../../types/JsonSchemaFormily";
import { convertDefaultFieldSchema } from "./default/default.converter";


export function convertButton(
  component: ComponentSchema
): FormilyFieldSchema {
  const p = component.properties;
  const base = convertDefaultFieldSchema(component);

  return {
    ...base,
    type: "void",
    "x-component": "CustomButton",
    "x-component-props": {
      ...base["x-component-props"],
      text: p.text,
      variant: p.variant,
      style: p.style || {},
      action: component.action || []
    },
    "x-index": component.sequence
  };
}