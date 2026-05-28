import axios from "axios";
import { MappingSchema } from "../dynamicPageRender/types/JsonSchema";
import { resolveApiMapping } from "./rule/api.resolver";


export async function resolveMapping(
  mapping?: MappingSchema
) {

  if (!mapping) return null;

  switch (mapping.type) {

    case "API":
      return resolveApiMapping(mapping);

    default:
      return null;
  }
}