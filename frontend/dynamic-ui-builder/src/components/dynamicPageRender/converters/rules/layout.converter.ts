import { ActionRegistry, ComponentSchema } from "../../types/JsonSchema";
import { FormilyFieldSchema } from "../../types/JsonSchemaFormily";
import { convertComponetToField } from "../componentsToFOrmily";
import { convertDefaultFieldSchema } from "./default/default.converter";

export function convertLayout(
  component: ComponentSchema,
  actionRegistry: ActionRegistry = {}
): FormilyFieldSchema {
  const p = component.properties || {};
  const base = convertDefaultFieldSchema(component, actionRegistry);

  const properties: Record<string, FormilyFieldSchema> = {};

  const children = [...(component.children || [])].sort(
    (a, b) => a.sequence - b.sequence
  );

  for (const child of children) {
    const converted = convertComponetToField(child, actionRegistry);
    if (!converted) continue;
    properties[child.name] = converted;
  }

  return {
    ...base,
    type: "void",
    "x-component": "Layout",
    "x-component-props": {
      ...base["x-component-props"],
      direction: p.direction || "column",
      gap: p.gap ?? 2,
      justifyContent: p.justifyContent,
      alignItems: p.alignItems,
      wrap: p.wrap,
      width: p.width,
      style: p.style || {},
    },
    properties,
  };
}
