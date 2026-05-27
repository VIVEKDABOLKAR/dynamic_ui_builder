import { ComponentSchema } from "../../types/JsonSchema";
import { FormilyFieldSchema } from "../../types/JsonSchemaFormily";

export function convertHeading(
  component: ComponentSchema
): FormilyFieldSchema {

  const p = component.properties || {};

  return {
    type: "void",

    "x-component": "Heading",

    "x-component-props": {
      componentId: component.id,
      text: p.text,
      variant: p.variant || "h4",
      align: p.align || "left",
      style: p.style || {},
    },

    "x-index": component.sequence
  };
}