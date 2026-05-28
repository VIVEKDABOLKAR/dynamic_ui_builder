import { resolveApiLookup } from "../../../dataMappingEngine/rule/api.resolver";
import { ComponentSchema } from "../../types/JsonSchema";

export async function convertSelect(
  component: ComponentSchema
) {

  const p = component.properties || {};

  const lookupResponse = await resolveApiLookup(component.lookup);

  const options = (lookupResponse || []).map((item: any) => ({
    label: item.displayValue,
    value: item.lookupValue
  }));

  return {

    type: "string",

    title: p.label,

    enum: options,

    "x-component": "Select",

    "x-component-props": {
      componentId: component.id,
      placeholder: p.placeholder,
      style: {
        width: p.width || "100%",
        ...(p.style || {})
      }
    },

    "x-index": component.sequence
  };
}