import { ComponentSchema } from "../../types/JsonSchema";

import { FormilyFieldSchema } from "../../types/JsonSchemaFormily";
import { convertComponetToField } from "../componentsToFOrmily";


export async function convertCard(
  component: ComponentSchema
): Promise<FormilyFieldSchema> {

  const p = component.properties || {};

  const properties: Record<string, any> = {};

  // recursive conversion
  const children = [...(component.children || [])]
    .sort((a, b) => a.sequence - b.sequence);

  for (const child of children) {

    const converted = await convertComponetToField(child);

    if (!converted) continue;

    properties[child.name] = converted;
  }

  return {
    type: "void",

    "x-component": "Card",

    "x-component-props": {
      componentId: component.id,
      title: p.title,
      description: p.description,
      width: p.width,
      style: p.style,
    },

    properties,

    "x-index": component.sequence,
  };
}