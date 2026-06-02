import { ActionRegistry, ComponentSchema } from "../../types/JsonSchema";
import { FormilyFieldSchema } from "../../types/JsonSchemaFormily";
import { convertDefaultFieldSchema } from "./default/default.converter";

export function convertSelect(
  component: ComponentSchema,
  actionRegistry: ActionRegistry = {}
): FormilyFieldSchema {

  const p = component.properties || {};
  const base = convertDefaultFieldSchema(component, actionRegistry);

  return {

    ...base,

    type: "string",

    title: p.label,

    enum: p.options || [],

    "x-component": "Select",

    "x-component-props": {
      ...base["x-component-props"],
      placeholder: p.placeholder,
      options: p.options || [],
      style: {
        width: p.width || "100%",
        ...(p.style || {})
      }
    },

    "x-index": component.sequence
  };
}