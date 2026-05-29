import { setNestedValue }
    from "./setNestedValue";

export function buildEntityPayload(

    values: any,

    schema: any
) {

    const payload = {};

    traverseSchema(

        schema,

        values,

        payload
    );

    return payload;
}

function traverseSchema(

  schema: any,

  values: any,

  payload: any
) {

  if (!schema?.properties) {
    return;
  }

  for (const fieldName in schema.properties) {

    const fieldSchema =
      schema.properties[fieldName];

    // current field value
    const fieldValue =
      values?.[fieldName];

    // entity mapping
    const mapping =
      fieldSchema["x-mapping"];

    // ONLY ENTITY mappings
    if (
      mapping?.type === "ENTITY"
      && mapping.source
      && mapping.source !== "."
    ) {

      setNestedValue(

        payload,

        mapping.source,

        fieldValue
      );
    }

    // RECURSIVE children traversal
    if (fieldSchema.properties) {

      traverseSchema(

        fieldSchema,

        values?.[fieldName] || {},

        payload
      );
    }
  }
}