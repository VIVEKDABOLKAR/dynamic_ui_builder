import { ComponentSchema } from "../../types/JsonSchema";

export function convertSelect(
  component: ComponentSchema
): any {

  const p = component.properties || {};

  return {

    type: "string",

    title: p.label,

    enum: p.options || [],

    "x-component": "Select",

    "x-component-props": {
      placeholder: p.placeholder,
      style: {
        width: p.width
      }
    },

    "x-index": component.sequence
  };
}