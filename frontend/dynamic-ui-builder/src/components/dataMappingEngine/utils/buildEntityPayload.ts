  import { setNestedValue } from "./setNestedValue";

export function buildEntityPayload(
  values: Record<string, any>,
  schema: any
) {
  const payload = {};

  traverseSchema(
    schema,
    values, // original form data
    payload
  );

  return payload;
}

function traverseSchema(
  schema: any,
  formValues: Record<string, any>,
  payload: any
) {
  if (!schema?.properties) {
    return;
  }

  for (const [fieldName, fieldSchema] of Object.entries<any>(
    schema.properties
  )) {
    const mapping = fieldSchema["x-mapping"];

    // Value always comes from original form data
    const fieldValue = formValues[fieldName];

    if (
      mapping?.type === "ENTITY" &&
      mapping.source &&
      mapping.source !== "." &&
      fieldValue !== undefined
    ) {
      setNestedValue(
        payload,
        mapping.source,
        fieldValue
      );
    }

    // Recursively traverse children
    if (fieldSchema.properties) {
      traverseSchema(
        fieldSchema,
        formValues,
        payload
      );
    }
  }
}