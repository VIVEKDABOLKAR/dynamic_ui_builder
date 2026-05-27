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
      componentId: component.id,

      placeholder: p.placeholder,

      multiline: true,

      minRows: p.rows || 4,
      width: p.width || "100%",
      height: p.height,
      style: p.style || {}
    },

    "x-index": component.sequence
  };
}