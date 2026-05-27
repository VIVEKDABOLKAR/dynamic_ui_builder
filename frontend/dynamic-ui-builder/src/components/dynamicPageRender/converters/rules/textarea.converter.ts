import { ComponentSchema } from "../../types/JsonSchema";

import { FormilyFieldSchema } from "../../types/JsonSchemaFormily";

export function convertTextarea(
  component: ComponentSchema
): FormilyFieldSchema {

  const p = component.properties || {};

  return {

    type: "string",

    title: p.label,

    required: p.required || false,

    "x-component": "Textarea",

    "x-component-props": {

      placeholder: p.placeholder,

      multiline: true,

      minRows: p.rows || 4,

      style: {
        width: p.width || "100%",
        height: p.height
      }
    },

    "x-index": component.sequence
  };
}