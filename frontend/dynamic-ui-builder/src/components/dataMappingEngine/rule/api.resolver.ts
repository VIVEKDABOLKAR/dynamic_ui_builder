import axios from "axios";
import { LookupSchema, MappingSchema } from "../../dynamicPageRender/types/JsonSchema";
import { API_REGISTRY } from "../apiRegistery/ApiRegistery";

function extractByPath(data: any, path?: string) {
  if (!path || !path.trim()) {
    return data;
  }

  return path
    .split(".")
    .filter(Boolean)
    .reduce((acc, key) => acc?.[key], data);
}

export async function resolveApiMapping(
  mapping: MappingSchema
) {

  try {

    if (!mapping.source || !mapping.source.trim()) {
      return [];
    }

    const registryEntry = (API_REGISTRY as any)[mapping.source];
    const isAbsoluteUrl = /^https?:\/\//i.test(mapping.source);
    const fallbackUrl = isAbsoluteUrl
      ? mapping.source
      : `http://localhost:8080${mapping.source}`;
    const url = registryEntry?.url || fallbackUrl;
    const method = (mapping.method || registryEntry?.method || "GET").toUpperCase();

    const response = await axios({
      method,
      url,
      headers: {},
      params: {},
    });

    const result = extractByPath(response.data, mapping.responsePath);

    return Array.isArray(result) ? result : (result ?? []);

  } catch (error) {

    console.error(
      "API Mapping Error",
      error
    );

    return [];
  }
}

export async function resolveApiLookup(
  lookup: LookupSchema | undefined
) {

  if(!lookup){
    return ;
  }
  try {

    const registryEntry = (API_REGISTRY as any)[lookup.apiUrl];
    const url = registryEntry?.url || `http://localhost:8080${lookup.apiUrl}`;

    const response = await axios({
      method: (registryEntry?.method || "GET").toUpperCase(),
      url,
      headers: {},
      params: {},
    });

    return response.data;

  } catch (error) {

    console.error(
      "API Mapping Error",
      error
    );

    return [];
  }
}