import { ComponentSchema } from "../../types/JsonSchema";
import { FormilyFieldSchema } from "../../types/JsonSchemaFormily";
import { convertDefaultFieldSchema } from "./default/default.converter";

export function convertHeading(
  component: ComponentSchema
): FormilyFieldSchema {

  const p = component.properties || {};
  const base = convertDefaultFieldSchema(component);

  return {
    ...base,
    type: "void",

    "x-component": "Heading",

    "x-component-props": {
      ...base["x-component-props"],
      text: p.text,
      variant: p.variant || "h4",
      align: p.align || "left",
      style: p.style || {},
    },

    "x-index": component.sequence
  };
}