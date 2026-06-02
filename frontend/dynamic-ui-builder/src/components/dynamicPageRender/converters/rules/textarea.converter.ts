import { ActionRegistry, ComponentSchema } from "../../types/JsonSchema";

import { FormilyFieldSchema } from "../../types/JsonSchemaFormily";
import { convertDefaultFieldSchema } from "./default/default.converter";

export function convertTextarea(
  component: ComponentSchema,
  actionRegistry: ActionRegistry = {}
): FormilyFieldSchema {

  const p = component.properties || {};
  const base = convertDefaultFieldSchema(component, actionRegistry);

  return {

    ...base,

    type: "string",

    title: p.label,

    required: p.required || false,

    "x-component": "Textarea",

    "x-component-props": {
      ...base["x-component-props"],

      placeholder: p.placeholder,

      multiline: true,

      width: p.width || "100%",
      height: p.height,
      style: p.style || {}
    },

    "x-index": component.sequence
  };
}