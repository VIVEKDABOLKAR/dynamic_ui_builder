import React, { useMemo } from 'react'
import { convertToFormilySchema } from './JsonConvert.ts'
import { createForm } from '@formily/core'
import { FormProvider } from '@formily/react'
import { SchemaField } from './formily/SchemaField.tsx'
import { PageSchemaContext } from './context/PageSchemaContext.ts'

/**
 * FIXES LIVE PREVIEW STALE-STATE ISSUE
 *
 * Previous problem:
 * - formilySchema was stored in React state.
 * - jsonSchema changed on every editor keystroke.
 * - conversion happened inside useEffect().
 * - useEffect runs AFTER render.
 * - therefore the preview rendered the OLD Formily schema first.
 * - Formily could also keep the previous field tree/state.
 *
 * New approach:
 * - derive Formily schema synchronously from jsonSchema with useMemo().
 * - every new previewSchema immediately produces a new Formily schema.
 * - SchemaField receives a deterministic key based on the schema.
 * - when an editor value changes, the old Formily field tree is discarded
 *   and rebuilt from the latest schema.
 */
export default function DynamicPreviewPageRenderEngine({ jsonSchema }) {
  const form = useMemo(() => createForm(), [])

  /*
   * IMPORTANT:
   * Do not keep this in useState/useEffect.
   *
   * jsonSchema is the source of truth for the preview.
   * When editorDraft changes, PagePreviewPanel creates a new jsonSchema
   * object. We must convert that object during the same render.
   */
  const formilySchema = useMemo(() => {
    if (!jsonSchema) return null

    return convertToFormilySchema(jsonSchema)
  }, [jsonSchema])

  /*
   * A schema key forces SchemaField to be recreated when any preview
   * property changes:
   *
   * label
   * placeholder
   * width
   * height
   * visibility
   * disabled
   * required
   * component type
   * button text
   * table title
   * options
   * etc.
   *
   * This avoids Formily retaining the previous rendered field structure.
   */
  const schemaKey = useMemo(() => {
    if (!formilySchema) return 'empty-preview'

    try {
      return JSON.stringify(formilySchema)
    } catch {
      return String(Date.now())
    }
  }, [formilySchema])

  if (!formilySchema) {
    return <div>Loading...</div>
  }

  return (
    <div className="bg-white text-black m-2 p-2">
      <FormProvider form={form}>
        <PageSchemaContext.Provider value={formilySchema}>
          <SchemaField
            key={schemaKey}
            schema={formilySchema}
          />
        </PageSchemaContext.Provider>
      </FormProvider>
    </div>
  )
}