import { ActionRegistry, ComponentSchema } from "../../types/JsonSchema";
import { convertDefaultFieldSchema } from "./default/default.converter";

export function convertTable(
  component: ComponentSchema,
  actionRegistry: ActionRegistry = {}
): any {

  const p = component.properties || {};
  const base = convertDefaultFieldSchema(component, actionRegistry);

  return {
    ...base,
    type: "void",

    "x-component": "DataTable",

    "x-mapping": component.mapping || undefined,

    "x-component-props": {
      ...base["x-component-props"],
      title: p.title,

      columns: p.columns || [],

      height: p.height || 400,

      mapping: component.mapping || {}
    },

    "x-index": component.sequence
  };
}