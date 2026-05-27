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
      componentId: component.id,
      placeholder: p.placeholder,
      style: {
        width: p.width || "100%",
        ...(p.style || {})
      }
    },

    "x-index": component.sequence
  };
}