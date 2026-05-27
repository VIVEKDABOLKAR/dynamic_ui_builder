import { ComponentSchema } from "../../types/JsonSchema";

export function convertRadio(
  component: ComponentSchema
): any {

  const p = component.properties || {};

  return {

    type: "string",

    title: p.label,

    default: p.defaultValue,

    enum: p.options || [],

    "x-component": "Radio",

    "x-component-props": {
      componentId: component.id,
      style: {
        width: p.width || "100%",
        ...(p.style || {})
      }
    },

    "x-index": component.sequence
  };
}