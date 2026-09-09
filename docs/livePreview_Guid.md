# Live Preview – Schema Update and Rendering Flow

## 1. Purpose

This document explains how the **Live Preview** feature works in the Dynamic UI Builder.

The main objective of Live Preview is:

> Whenever a user changes a component property in the editor, the preview must immediately reflect the new value without requiring the user to click **Update Component** or save the page.

For example:

```text
User changes Label
        ↓
Editor Draft changes
        ↓
Preview Schema is rebuilt
        ↓
Formily Schema is regenerated
        ↓
Preview component is recreated
        ↓
New value appears immediately
```

---

# 2. High-Level Architecture

The Live Preview consists of three important layers:

```text
┌──────────────────────────────┐
│        Component Editor      │
│                              │
│ label / width / required     │
│ disabled / placeholder / ... │
└──────────────┬───────────────┘
               │
               │ editorDraft
               ▼
┌──────────────────────────────┐
│      PagePreviewPanel        │
│                              │
│ Applies temporary changes    │
│ to page schema               │
└──────────────┬───────────────┘
               │
               │ previewSchema
               ▼
┌──────────────────────────────┐
│ DynamicPageRenderEngine      │
│                              │
│ Converts JSON → Formily      │
│ schema                       │
└──────────────┬───────────────┘
               │
               │ formilySchema
               ▼
┌──────────────────────────────┐
│        SchemaField           │
│                              │
│ Renders the actual preview  │
└──────────────────────────────┘
```

There are two different concepts that must not be confused:

1. **Page JSON Schema**
2. **Formily Schema**

The editor primarily works with the application's Page JSON Schema.

The renderer converts that schema into the Formily schema required for rendering.

---

# 3. The Three Important Schemas

## 3.1 Saved Page Schema

This is the schema stored by the application.

Example:

```json
{
  "components": [
    {
      "id": 101,
      "name": "firstName",
      "type": "input",
      "properties": {
        "label": "First Name",
        "placeholder": "Enter first name",
        "required": true,
        "width": "300px"
      }
    }
  ]
}
```

This represents the persisted page configuration.

---

## 3.2 Editor Draft

When the user selects a component, the editor creates an editable object.

Example:

```js
editorDraft = {
  componentName: "firstName",
  componentType: "input",
  labelName: "Customer Name",
  placeholder: "Enter customer name",
  width: "400px",
  isVisible: true,
  isDisabled: false,
  isRequired: true
}
```

This object represents the **current unsaved editor values**.

It is important that `editorDraft` is not immediately treated as the saved schema.

---

## 3.3 Live Preview Schema

The preview needs a temporary version of the page schema.

For example:

### Original

```json
{
  "label": "First Name",
  "placeholder": "Enter first name",
  "width": "300px"
}
```

### User changes the editor

```text
Label:
First Name
      ↓
Customer Name
```

The preview schema becomes:

```json
{
  "label": "Customer Name",
  "placeholder": "Enter first name",
  "width": "300px"
}
```

This schema is temporary.

The database/page schema is not modified until the user explicitly saves or updates the component.

---

# 4. Complete Live Preview Flow

The complete flow is:

```text
                     USER
                      │
                      │ changes field
                      ▼
              Component Editor
                      │
                      │ onChange
                      ▼
                editorDraft
                      │
                      │ React state update
                      ▼
              PagePreviewPanel
                      │
                      │ applyLiveEditToPageJson()
                      ▼
              previewSchema
                      │
                      │ new object
                      ▼
       DynamicPageRenderEngine
                      │
                      │ convertToFormilySchema()
                      ▼
              formilySchema
                      │
                      │ new schema
                      ▼
                 SchemaField
                      │
                      ▼
                LIVE PREVIEW
```

---

# 5. Why We Need `editorDraft`

Suppose the saved component is:

```json
{
  "label": "Name"
}
```

The user changes it to:

```text
Customer Name
```

We do not want to immediately change the saved page schema.

Instead:

```text
Saved Schema
    │
    ├── label = "Name"
    │
    └───────────────┐
                    │
                    ▼
              editorDraft
                    │
                    └── label = "Customer Name"
```

The preview combines these two pieces of information.

Conceptually:

```js
previewSchema = merge(
  savedPageSchema,
  editorDraft
)
```

This allows the user to experiment with changes before saving.

---

# 6. `applyLiveEditToPageJson()`

The main responsibility of the preview schema builder is to create a temporary schema.

The important rule is:

> Never mutate the original saved page schema.

Therefore we first clone it:

```js
const clone = structuredClone(pageJson)
```

Then we find the selected component:

```js
patchComponentById(
  clone.components,
  selectedComponentId,
  editorDraft
)
```

Then the selected component receives the latest editor values.

Example:

```js
node.properties = {
  ...previousProperties,

  label: editorDraft.labelName,
  placeholder: editorDraft.placeholder,
  width: editorDraft.width,

  visible: !!editorDraft.isVisible,
  disabled: !!editorDraft.isDisabled,
  required: !!editorDraft.isRequired
}
```

The result is a new temporary page schema.

---

# 7. Why Cloning Is Important

We must not do this:

```js
pageJson.components[0].properties.label =
  editorDraft.labelName
```

This directly modifies the original object.

That can cause several problems:

* React may not detect the expected change.
* Saved state can accidentally change.
* Undo/redo becomes difficult.
* Editor state and preview state become coupled.
* Other components can unexpectedly receive changes.

Instead:

```js
const previewSchema = structuredClone(pageJson)

patchComponentById(
  previewSchema.components,
  selectedComponentId,
  editorDraft
)
```

Now we have:

```text
Saved Page Schema
       │
       │ clone
       ▼
Preview Page Schema
       │
       │ patch selected component
       ▼
Temporary Live Preview
```

---

# 8. Why `useMemo()` Is Used

The previous implementation converted the schema inside an effect.

Conceptually:

```js
useEffect(() => {
  setFormilySchema(
    convertToFormilySchema(jsonSchema)
  )
}, [jsonSchema])
```

This creates an extra render cycle.

The sequence becomes:

```text
jsonSchema changes
       ↓
React renders
       ↓
OLD formilySchema is still available
       ↓
Preview renders OLD value
       ↓
useEffect executes
       ↓
formilySchema changes
       ↓
React renders again
       ↓
NEW value appears
```

This can produce stale preview behavior.

The current implementation derives the Formily schema synchronously:

```js
const formilySchema = useMemo(() => {
  if (!jsonSchema) return null

  return convertToFormilySchema(jsonSchema)
}, [jsonSchema])
```

Now the flow is:

```text
jsonSchema changes
       ↓
useMemo recalculates
       ↓
NEW formilySchema
       ↓
SchemaField renders NEW schema
```

There is no intermediate state containing the previous schema.

---

# 9. Why `SchemaField` Needs a New Key

Formily does more than simply render JSON.

It creates an internal field/component tree.

For example:

```text
SchemaField
   │
   ├── Field: firstName
   │
   └── Input
```

If only the schema object changes, Formily may retain parts of the previous field tree.

For that reason, we generate a key from the current schema:

```js
const schemaKey = JSON.stringify(formilySchema)
```

Then:

```jsx
<SchemaField
  key={schemaKey}
  schema={formilySchema}
/>
```

When the schema changes:

```text
OLD SCHEMA
{
  label: "Name"
}

        ↓

NEW SCHEMA
{
  label: "Customer Name"
}
```

The key changes:

```text
key(oldSchema)
       ↓
key(newSchema)
```

React therefore recreates the `SchemaField`.

The rendering lifecycle becomes:

```text
Old SchemaField
       ↓
schema changed
       ↓
key changed
       ↓
Old SchemaField removed
       ↓
New SchemaField created
       ↓
New Formily fields created
       ↓
Updated preview
```

---

# 10. Formily Schema Conversion

The application's Page JSON Schema is not necessarily the exact structure expected by Formily.

Therefore:

```js
convertToFormilySchema(jsonSchema)
```

performs the conversion.

Conceptually:

```text
Application Schema
        │
        │ convertToFormilySchema()
        ▼
Formily Schema
```

Example application schema:

```json
{
  "name": "customerName",
  "type": "input",
  "properties": {
    "label": "Customer Name",
    "placeholder": "Enter customer name",
    "required": true
  }
}
```

Possible Formily representation:

```json
{
  "type": "object",
  "properties": {
    "customerName": {
      "type": "string",
      "required": true,
      "x-decorator": "FormItem",
      "x-component": "Input",
      "x-component-props": {
        "placeholder": "Enter customer name"
      },
      "x-decorator-props": {
        "label": "Customer Name"
      }
    }
  }
}
```

The important point is that **the preview does not directly render the editor draft**.

The draft first modifies the application schema.

Then the application schema is converted into Formily schema.

---

# 11. Example: Changing a Label

Initial state:

```text
Editor:
Label = First Name

Saved Schema:
label = First Name

Preview:
First Name
```

User types:

```text
Customer Name
```

React updates:

```js
editorDraft.labelName = "Customer Name"
```

Then:

```js
applyLiveEditToPageJson()
```

creates:

```json
{
  "label": "Customer Name"
}
```

Then:

```js
convertToFormilySchema()
```

creates a new Formily schema.

Then:

```jsx
<SchemaField key={newSchemaKey} />
```

is recreated.

Final result:

```text
Editor:
Customer Name

Saved Schema:
First Name

Preview:
Customer Name
```

This is exactly what we want.

The preview shows the unsaved value while the saved schema remains unchanged.

---

# 12. Example: Changing Width

Initial:

```json
{
  "width": "300px"
}
```

User changes:

```text
300px → 500px
```

Editor:

```js
editorDraft.width = "500px"
```

Preview patch:

```js
node.properties.width = editorDraft.width
```

Temporary schema:

```json
{
  "width": "500px"
}
```

Formily conversion:

```text
Page Schema
    ↓
width = 500px
    ↓
Formily Schema
    ↓
SchemaField
    ↓
Preview width = 500px
```

---

# 13. Example: Clearing a Value

This is an important edge case.

Suppose:

```json
{
  "height": "100px"
}
```

The user deletes the height value.

The new editor value is:

```js
height = ""
```

We must NOT do:

```js
height: editorDraft.height || oldHeight
```

Because:

```js
"" || "100px"
```

returns:

```text
100px
```

Therefore the preview would incorrectly keep the old value.

Instead:

```js
if (
  Object.prototype.hasOwnProperty.call(
    additional,
    "height"
  )
) {
  node.properties.height = additional.height
}
```

Now:

```text
Editor value:
""

        ↓

Preview schema:
height = ""

        ↓

Preview:
height removed/reset
```

---

# 14. Additional Properties

Live Preview must also patch additional editor properties.

Examples:

```text
height
border
borderRadius
className
```

The general rule is:

```js
for (const [key, value] of Object.entries(additional)) {
  node.properties[key] = value
}
```

This allows new editor properties to be added later without rewriting the complete preview logic.

---

# 15. Table Preview

Tables can have properties that are different from normal form fields.

For example:

```js
editorDraft.labelName
```

may represent the table title.

Therefore the preview builder explicitly maps it:

```js
if (editorDraft.componentType === "table") {
  node.properties.title =
    editorDraft.labelName ||
    editorDraft.componentName
}
```

So:

```text
Editor:
Table Title = Customers

        ↓

Preview Schema:
title = Customers

        ↓

Rendered Table:
Customers
```

---

# 16. Button Preview

Buttons similarly use their own property:

```js
if (editorDraft.componentType === "button") {
  node.properties.text =
    editorDraft.labelName ||
    editorDraft.componentName
}
```

Therefore:

```text
Editor:
Button Text = Submit Form

        ↓

Preview Schema:
text = Submit Form

        ↓

Preview:
[ Submit Form ]
```

---

# 17. Select / Radio Options

Select and radio components have another important live-preview requirement.

If the editor changes lookup/options:

```text
Option A
Option B
```

to:

```text
Option A
Option B
Option C
```

the preview schema must also receive the new options.

The preview builder therefore generates options from the current editor draft:

```js
node.properties.options = (
  editorDraft.lookupValues || []
).map((item) => ({
  label:
    item?.displayValue ??
    item?.lookupValue ??
    "",

  value:
    item?.lookupValue ??
    item?.value ??
    ""
}))
```

The flow becomes:

```text
Lookup Editor
     ↓
lookupValues
     ↓
Preview Schema
     ↓
Formily Schema
     ↓
Select / Radio
     ↓
New option appears
```

---

# 18. React Dependency Chain

The most important React dependency chain is:

```text
editorDraft
    ↓
previewSchema
    ↓
formilySchema
    ↓
SchemaField key
```

Implementation:

```js
const previewSchema = useMemo(() => {
  return applyLiveEditToPageJson(
    pageJson,
    selectedComponentId,
    editorDraft
  )
}, [
  pageJson,
  selectedComponentId,
  editorDraft
])
```

Then:

```js
const formilySchema = useMemo(() => {
  return convertToFormilySchema(jsonSchema)
}, [jsonSchema])
```

Then:

```js
const schemaKey = JSON.stringify(formilySchema)
```

Then:

```jsx
<SchemaField
  key={schemaKey}
  schema={formilySchema}
/>
```

This creates the complete reactive chain.

---

# 19. What Must Not Be Done

## Do not mutate the saved schema

Bad:

```js
pageJson.components[index].properties.label =
  editorDraft.labelName
```

Good:

```js
const previewSchema = structuredClone(pageJson)

previewSchema.components[index].properties.label =
  editorDraft.labelName
```

---

## Do not keep derived Formily schema in state

Avoid:

```js
const [formilySchema, setFormilySchema] =
  useState(null)
```

followed by:

```js
useEffect(() => {
  setFormilySchema(
    convertToFormilySchema(jsonSchema)
  )
}, [jsonSchema])
```

Prefer:

```js
const formilySchema = useMemo(
  () => convertToFormilySchema(jsonSchema),
  [jsonSchema]
)
```

---

## Do not fall back to old values when the user clears a field

Bad:

```js
value || oldValue
```

Good:

```js
if (hasOwnProperty.call(source, key)) {
  target[key] = source[key]
}
```

---

## Do not use one global Formily form for every preview

The preview should own its form instance:

```js
const form = useMemo(
  () => createForm(),
  []
)
```

Then:

```jsx
<FormProvider form={form}>
  ...
</FormProvider>
```

This prevents unrelated Formily state from leaking into the preview.

---

# 20. Final Data Flow

The complete implementation can be summarized as:

```text
┌───────────────────────┐
│     Saved Page JSON   │
└───────────┬───────────┘
            │
            │ clone
            ▼
┌───────────────────────┐
│   Preview Page JSON   │
│                       │
│ + editorDraft values  │
└───────────┬───────────┘
            │
            │ convertToFormilySchema()
            ▼
┌───────────────────────┐
│    Formily Schema     │
└───────────┬───────────┘
            │
            │ schema key changes
            ▼
┌───────────────────────┐
│      SchemaField      │
│     recreated         │
└───────────┬───────────┘
            │
            ▼
┌───────────────────────┐
│      Live Preview     │
└───────────────────────┘
```

---

# 21. Source of Truth

The application has three different states:

| State           | Purpose                                    |
| --------------- | ------------------------------------------ |
| `pageJson`      | Persisted/current page configuration       |
| `editorDraft`   | Current unsaved editor values              |
| `previewSchema` | Temporary combination used only by preview |

The relationship is:

```text
pageJson
   +
editorDraft
   ↓
previewSchema
   ↓
Formily Schema
   ↓
Preview
```

The preview should **never become the source of truth**.

---

# 22. Why the Fix Works

The original problem was essentially caused by a stale rendering chain:

```text
Editor Change
     ↓
editor state changes
     ↓
preview receives changed data
     ↓
Formily schema still represents old render tree
     ↓
OLD PREVIEW
```

The fixed implementation forces the complete chain to update:

```text
Editor Change
     ↓
editorDraft changes
     ↓
new previewSchema
     ↓
new Formily schema
     ↓
new SchemaField key
     ↓
new Formily render tree
     ↓
UPDATED PREVIEW
```

Therefore the preview now behaves as a true **live preview** rather than a preview that only reflects changes after an explicit component update.

---

# 23. Maintenance Rule

Whenever a new editor property is introduced, follow this sequence:

```text
1. Add property to editorDraft
        ↓
2. Add property to preview schema patching
        ↓
3. Verify convertToFormilySchema supports it
        ↓
4. Verify rendered component consumes it
        ↓
5. Test changing the property without saving
        ↓
6. Test clearing the property
        ↓
7. Test changing it multiple times
```

Example for a new property:

```text
minLength
```

The implementation should support:

```text
Editor
  ↓
editorDraft.minLength
  ↓
previewSchema.properties.minLength
  ↓
Formily Schema
  ↓
Rendered component
```

---

# 24. Testing Checklist

Every Live Preview property should be tested for:

* [ ] Initial value renders correctly
* [ ] Changing value updates preview immediately
* [ ] Changing value multiple times updates correctly
* [ ] Clearing value removes/resets it
* [ ] Saved schema is not modified
* [ ] Switching to another component works
* [ ] Switching back preserves expected editor state
* [ ] Required state updates
* [ ] Disabled state updates
* [ ] Visibility updates
* [ ] Width updates
* [ ] Height updates
* [ ] Border updates
* [ ] Border radius updates
* [ ] Placeholder updates
* [ ] Button text updates
* [ ] Table title updates
* [ ] Select options update
* [ ] Radio options update

---

# 25. Key Principle

The most important architectural rule is:

> **Editor state drives the temporary preview schema; the temporary preview schema drives Formily; Formily drives the rendered preview.**

In short:

```text
EDITOR
  ↓
editorDraft
  ↓
previewSchema
  ↓
FormilySchema
  ↓
SchemaField
  ↓
PREVIEW
```

The saved page schema should remain untouched until the user explicitly saves the component.
