import { ComponentSchema } from "../../types/JsonSchema";
import { FormilyFieldSchema } from "../../types/JsonSchemaFormily";

export function convertSelect(
  component: ComponentSchema
): FormilyFieldSchema {

  const p = component.properties || {};

  return {

    type: "string",

    title: p.label,

    enum: p.options || [],

    "x-component": "Select",

    "x-lookup": component.lookup || undefined,

    "x-mapping": component.mapping || undefined,

    "x-component-props": {
      componentId: component.id,
      placeholder: p.placeholder,
      lookup: component.lookup || null,
      mapping: component.mapping || null,
      options: p.options || [],
      style: {
        width: p.width || "100%",
        ...(p.style || {})
      }
    },

    "x-index": component.sequence
  };
}