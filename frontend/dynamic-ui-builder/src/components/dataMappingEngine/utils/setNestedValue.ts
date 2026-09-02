export function setNestedValue(
  obj: Record<string, any>,
  path: string,
  value: any
) {
  const keys = path.split(".");

  let current = obj;

  for (let i = 0; i < keys.length; i++) {
    const key = keys[i];

    if (i === keys.length - 1) {
      current[key] = value;
      return;
    }

    if (
      !current[key] ||
      typeof current[key] !== "object"
    ) {
      current[key] = {};
    }

    current = current[key];
  }
}