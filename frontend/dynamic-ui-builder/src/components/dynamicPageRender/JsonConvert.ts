import { convertPageToFormily } from "./converters/pageToFormily";
import { DynamicPageSchema } from "./types/JsonSchema";

//this will provied function to convert our define json scheama into formily json schema
export function convertToFormilySchema(jsonSchema: DynamicPageSchema | string) {
    //convert jsonSchema string into json object


    const res = convertPageToFormily(normalizePageSchema(jsonSchema));

    console.log(res)
    return res;
}

function normalizePageSchema(input: DynamicPageSchema | string): DynamicPageSchema | null {
    if (!input) {
        return null;
    }

    if (typeof input === "string") {
        try {
            return JSON.parse(input) as DynamicPageSchema;
        } catch (error) {
            console.error("Failed to parse page schema JSON string:", error);
            return null;
        }
    }

    return input;
}