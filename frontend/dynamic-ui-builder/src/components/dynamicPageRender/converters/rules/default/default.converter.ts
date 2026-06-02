import { ActionRegistry, ComponentSchema } from "../../../types/JsonSchema";
import { FormilyFieldSchema } from "../../../types/JsonSchemaFormily";
import { resolveComponentActions } from "../../../actionRenders/ActionDispacther";

export function convertDefaultFieldSchema(
  component: ComponentSchema,
  actionRegistry: ActionRegistry = {}
): FormilyFieldSchema {
  const properties = component.properties || {};

  return {
    title: properties.label,
    description: properties.description,
    default: properties.defaultValue,
    "x-index": component.sequence,
    "x-visible": properties.visible ?? true,
    "x-disabled": properties.disabled ?? false,
    "x-mapping": component.mapping || undefined,
    "x-lookup": component.lookup || undefined,
    "x-component-props": {
      componentId: component.id,
      componentName: component.name,
      label: properties.label,
      placeholder: properties.placeholder,
      text: properties.text,
      width: properties.width,
      height: properties.height,
      disabled: properties.disabled ?? false,
      visible: properties.visible ?? true,
      required: properties.required ?? false,
      defaultValue: properties.defaultValue,
      options: properties.options || [],
      style: properties.style || {},
      action: component.action,
      mapping: component.mapping || undefined,
      lookup: component.lookup || undefined,
    },
  };
}
