import { ActionRegistry, ComponentSchema } from "../../types/JsonSchema";
import { FormilyFieldSchema } from "../../types/JsonSchemaFormily";
import { convertDefaultFieldSchema } from "./default/default.converter";

export function convertDatePicker(
  component: ComponentSchema,
  actionRegistry: ActionRegistry = {}
): FormilyFieldSchema {

  const p = component.properties || {};
  const base = convertDefaultFieldSchema(component, actionRegistry);

  return {

    ...base,

    type: "date",

    title: p.label,

    default: p.defaultValue || false,

    "x-component": "Datepicker",

    "x-component-props": {
      ...base["x-component-props"],
      "format": p.formate || "DD/MM/YYYY",
      style: p.style || {}
    },

    "x-index": component.sequence
  };
}