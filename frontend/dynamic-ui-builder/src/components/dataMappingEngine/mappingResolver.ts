import axios from "axios";
import { MappingSchema } from "../dynamicPageRender/types/JsonSchema";
import { resolveApiMapping } from "./rule/api.resolver";
import resolveEntityMapping from "./rule/entity.resolver";


export async function resolveMapping(
  mapping?: MappingSchema
) {

  if (!mapping) return null;

  switch (mapping.type) {

    case "API":
      return resolveApiMapping(mapping);

    case "ENTITY":
      return resolveEntityMapping(mapping);

    default:
      return null;
  }
}