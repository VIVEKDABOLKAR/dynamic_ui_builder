import { ComponentSchema } from "../../types/JsonSchema";

export function convertCheckbox(
  component: ComponentSchema
): any {

  const p = component.properties || {};

  return {

    type: "boolean",

    title: p.label,

    default: p.defaultValue || false,

    "x-component": "Checkbox",

    "x-index": component.sequence
  };
}