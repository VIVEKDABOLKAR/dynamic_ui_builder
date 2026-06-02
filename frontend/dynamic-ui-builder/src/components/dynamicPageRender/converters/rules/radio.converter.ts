import { ActionRegistry, ComponentSchema } from "../../types/JsonSchema";
import { FormilyFieldSchema } from "../../types/JsonSchemaFormily";
import { convertDefaultFieldSchema } from "./default/default.converter";

export function convertRadio(
  component: ComponentSchema,
  actionRegistry: ActionRegistry = {}
): FormilyFieldSchema {

  const p = component.properties || {};
  const base = convertDefaultFieldSchema(component, actionRegistry);

  return {

    ...base,

    type: "string",

    title: p.label,

    default: p.defaultValue,

    enum: p.options || [],

    "x-component": "Radio",

    "x-lookup": component.lookup || undefined,

    "x-mapping": component.mapping || undefined,

    "x-component-props": {
      ...base["x-component-props"],
      style: {
        width: p.width || "100%",
        ...(p.style || {})
      }
    },

    "x-index": component.sequence
  };
}