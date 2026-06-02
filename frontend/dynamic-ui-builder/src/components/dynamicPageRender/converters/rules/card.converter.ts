import { ActionRegistry, ComponentSchema } from "../../types/JsonSchema";

import { FormilyFieldSchema } from "../../types/JsonSchemaFormily";
import { convertComponetToField } from "../componentsToFOrmily";
import { convertDefaultFieldSchema } from "./default/default.converter";


export function convertCard(
  component: ComponentSchema,
  actionRegistry: ActionRegistry = {}
): FormilyFieldSchema {

  const p = component.properties || {};
  const base = convertDefaultFieldSchema(component, actionRegistry);

  const properties: Record<string, any> = {};

  // recursive conversion
  const children = [...(component.children || [])]
    .sort((a, b) => a.sequence - b.sequence);

  for (const child of children) {

    const converted = convertComponetToField(child, actionRegistry);

    if (!converted) continue;

    properties[child.name] = converted;
  }

  return {
    ...base,
    type: "void",

    "x-component": "Card",

    "x-component-props": {
      ...base["x-component-props"],
      title: p.title || p.label,
      description: p.description,
      width: p.width,
      style: p.style,
    },

    properties,

    "x-index": component.sequence,
  };
}