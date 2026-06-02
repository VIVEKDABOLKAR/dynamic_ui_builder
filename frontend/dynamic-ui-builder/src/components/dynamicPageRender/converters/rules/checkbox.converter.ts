import { ActionRegistry, ComponentSchema } from "../../types/JsonSchema";
import { FormilyFieldSchema } from "../../types/JsonSchemaFormily";
import { convertDefaultFieldSchema } from "./default/default.converter";

export function convertCheckbox(
  component: ComponentSchema,
  actionRegistry: ActionRegistry = {}
): FormilyFieldSchema {

  const p = component.properties || {};
  const base = convertDefaultFieldSchema(component, actionRegistry);

  return {

    ...base,

    type: "boolean",

    title: p.label,

    default: p.defaultValue || false,

    "x-component": "Checkbox",

    "x-component-props": {
      ...base["x-component-props"],
      style: p.style || {}
    },

    "x-index": component.sequence
  };
}