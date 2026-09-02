  import { setNestedValue } from "./setNestedValue";

export function buildEntityPayload(
  values: Record<string, any>,
  schema: any
) {
  const payload: Record<string, any> = {};

  traverseSchema(schema, values, payload);

  return payload;
}

function traverseSchema(
  schema: any,
  formValues: Record<string, any>,
  payload: Record<string, any>
) {
  if (!schema?.properties) {
    return;
  }

  for (const [fieldName, fieldSchema] of Object.entries<any>(
    schema.properties
  )) {
    // The actual value comes from the form field name.
    // Example: formValues["Text Field"] => "John"
    const fieldValue = formValues[fieldName];

    const mapping = fieldSchema?.["x-mapping"];

    // ENTITY field
    if (
      mapping?.type === "ENTITY" &&
      mapping.source &&
      mapping.source !== "." 
    ) {
      setNestedValue(
        payload,
        mapping.source,
        fieldValue
      );
    }

    // Recursively process nested schema properties
    if (fieldSchema?.properties) {
      traverseSchema(
        fieldSchema,
        formValues,
        payload
      );
    }
  }
}