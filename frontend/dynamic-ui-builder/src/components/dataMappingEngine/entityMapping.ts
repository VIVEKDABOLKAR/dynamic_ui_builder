import { FormilyFieldSchema, FormilyPageSchema } from "../dynamicPageRender/types/JsonSchemaFormily";
import { setNestedValue } from "./utils/setNestedValue";

export interface FieldMapping {
  /** The Formily field key — same as the component's name. */
  name: string;
  /** The entity mapping path, e.g. "jobOrder.driver.name". */
  path: string;
}

type SchemaNode = FormilyFieldSchema | FormilyPageSchema | null | undefined;

/**
 * Walk the Formily schema tree (page -> fields -> nested fields under
 * layout/card containers) and collect every field that carries an ENTITY
 * mapping with a real source path.
 *
 * This is the single place that understands "component -> payload path".
 * Submit-payload building, setFieldValue/getFieldValue resolution and the
 * dev inspection helpers all go through this, so they can never drift out
 * of sync with each other.
 */
export function collectMappings(schema: SchemaNode): FieldMapping[] {
  const mappings: FieldMapping[] = [];

  const walk = (node: SchemaNode) => {
    if (!node?.properties) return;

    for (const [name, field] of Object.entries(node.properties)) {
      const mapping = (field as FormilyFieldSchema)?.["x-mapping"];

      if (mapping?.type === "ENTITY" && mapping.source) {
        mappings.push({ name, path: mapping.source });
      }

      // layout/card fields are type "void" and hold their children under
      // their own nested `properties` — recurse so nested fields are found.
      walk(field as FormilyFieldSchema);
    }
  };

  walk(schema);
  return mappings;
}

/**
 * Two mapping paths conflict when one is a prefix of the other (e.g.
 * "user.name" and "user.name.first") — the same key would need to be both
 * a plain value and a nested object. Reported rather than silently
 * producing broken JSON.
 */
export function detectMappingConflicts(mappings: FieldMapping[]) {
  const conflicts: { a: FieldMapping; b: FieldMapping }[] = [];

  for (let i = 0; i < mappings.length; i++) {
    for (let j = i + 1; j < mappings.length; j++) {
      const a = mappings[i];
      const b = mappings[j];
      if (a.path === b.path || a.path.startsWith(`${b.path}.`) || b.path.startsWith(`${a.path}.`)) {
        conflicts.push({ a, b });
      }
    }
  }

  return conflicts;
}

/** Read a nested value by dot path, e.g. getFieldValue(payload, "jobOrder.driver.name"). */
export function getFieldValue(source: any, path?: string) {
  if (!path) return undefined;
  return path.split(".").reduce((current, key) => (current == null ? undefined : current[key]), source);
}

/** Write a nested value by dot path, using the same path parser as everywhere else. */
export function setFieldValue(target: any, path: string, value: any) {
  setNestedValue(target, path, value);
  return target;
}

/** Nested structure of every mapped path with empty ("") leaf values. */
export function buildEmptyPayload(schema: SchemaNode) {
  const payload = {};
  for (const { path } of collectMappings(schema)) {
    setNestedValue(payload, path, "");
  }
  return payload;
}

/**
 * Builds the real submit payload: mapped fields are nested under their
 * entity mapping path; any field with no mapping falls back to a flat
 * top-level key using its component name, exactly as before. This keeps
 * existing pages (with no entity mapping configured) submitting unchanged.
 */
export function buildSubmitPayload(schema: SchemaNode, formValues: Record<string, any> = {}) {
  const payload: Record<string, any> = {};
  const mappings = collectMappings(schema);
  const mappedNames = new Set(mappings.map((m) => m.name));

  for (const { name, path } of mappings) {
    const value = formValues[name];
    if (value !== undefined) {
      setNestedValue(payload, path, value);
    }
  }

  for (const [name, value] of Object.entries(formValues)) {
    if (!mappedNames.has(name) && value !== undefined) {
      payload[name] = value;
    }
  }

  return payload;
}

/**
 * SET_FIELD_VALUE / FETCH_DATA actions target a field by name. Admins can
 * now reference either the component name (existing behavior, unchanged)
 * or the entity mapping path (e.g. "jobOrder.driver.name") — this resolves
 * a mapping-path reference back to the Formily field name that must
 * actually be written to for the UI to update.
 */
export function resolveFieldName(schema: SchemaNode, fieldRef?: string) {
  if (!fieldRef) return fieldRef;

  const byPath = collectMappings(schema).find((m) => m.path === fieldRef);
  return byPath ? byPath.name : fieldRef;
}

/** Dev-only helper: prints the full nested payload shape for the current page. */
export function printPayloadStructure(schema: SchemaNode) {
  const mappings = collectMappings(schema);
  const conflicts = detectMappingConflicts(mappings);

  console.log("===== DYNAMIC PAGE PAYLOAD STRUCTURE =====");
  console.log(JSON.stringify(buildEmptyPayload(schema), null, 2));

  if (conflicts.length) {
    console.warn("Mapping conflicts detected:");
    conflicts.forEach(({ a, b }) =>
      console.warn(`  "${a.path}" (${a.name}) conflicts with "${b.path}" (${b.name})`)
    );
  }
}

/** Dev-only helper: prints the entity hierarchy + the raw list of mapped paths. */
export function printPageEntityStructure(schema: SchemaNode) {
  console.log("===== PAGE ENTITY STRUCTURE =====");
  console.log(JSON.stringify(buildEmptyPayload(schema), null, 2));

  console.log("\nMAPPINGS");
  collectMappings(schema).forEach(({ name, path }) => console.log(`${path}  (<- ${name})`));
}
