import { DynamicPageSchema } from "../types/JsonSchema";
import { FormilyPageSchema, FormilyFieldSchema } from "../types/JsonSchemaFormily";
import { convertComponetToField } from "./componentsToFOrmily";



export function convertPageToFormily(
    page: DynamicPageSchema | null
): FormilyPageSchema {


    // safety check
    if (!page || !Array.isArray(page.components)) {
        console.error("Invalid page schema:", page);

        return {
            type: "object",
            properties: {}
        };
    }

    const properties: Record<string, FormilyFieldSchema> = {};

    const sorted = [...page.components].sort(
        (a, b) => a.sequence - b.sequence
    );

    for (const comp of sorted) {
        const field = convertComponetToField(comp, page.actions || {});


        if (field) {
            properties[comp.name] = field;
        }
        

    }

    return {
        type: "object",
        properties,
        "x-actions": page.actions || {}
    }

}
