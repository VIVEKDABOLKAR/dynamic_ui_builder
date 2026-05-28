import { ComponentSchema } from "../../types/JsonSchema";

export function convertTable(
  component: ComponentSchema
): any {

  const p = component.properties || {};

  return {

    type: "void",

    "x-component": "DataTable",

    "x-mapping": component.mapping || undefined,

    "x-component-props": {

      title: p.title,

      columns: p.columns || [],

      height: p.height || 400,

      mapping: component.mapping || {}
    },

    "x-index": component.sequence
  };
}