import axios from "axios";
import { LookupSchema, MappingSchema } from "../../dynamicPageRender/types/JsonSchema";
import { API_REGISTRY } from "../apiRegistery/ApiRegistery";

export async function resolveApiMapping(
  mapping: MappingSchema
) {

  try {

    const registeryEntry = (API_REGISTRY as any)[mapping.source];
    if (!registeryEntry || !registeryEntry.url) {
        registeryEntry.url = mapping.source; // treat source as URL if not found in registry
    }

    const response = await axios({
      method: (registeryEntry.method || "GET").toUpperCase(),
      url: registeryEntry.url,
      headers: {},
      params: {},
    });

    let result = response.data;

    // resolve responsePath
    // if (result) {

    //   const paths = mapping.responsePath.split(".");

    //   for (const key of paths) {

    //     result = result?.[key];
    //   }
    // }

    return result;

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

    let registeryEntry = (API_REGISTRY as any)[lookup.apiUrl];
    if (!registeryEntry || !registeryEntry.url) {
        registeryEntry = `http://localhost:8080${lookup.apiUrl}`; // treat source as URL if not found in registry
    }

    const response = await axios({
      method: (registeryEntry?.method || "GET").toUpperCase(),
      url: registeryEntry,
      headers: {},
      params: {},
    });

    let result = response.data;
    console.log(result)

    // resolve responsePath
    // if (result) {

    //   const paths = mapping.responsePath.split(".");

    //   for (const key of paths) {

    //     result = result?.[key];
    //   }
    // }

    return result;

  } catch (error) {

    console.error(
      "API Mapping Error",
      error
    );

    return [];
  }
}