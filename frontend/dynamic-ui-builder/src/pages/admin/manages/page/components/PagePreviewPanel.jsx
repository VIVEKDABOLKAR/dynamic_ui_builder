import React, { useMemo } from 'react'
import { applyLiveEditToPageJson } from '../../../../../components/dynamicPageRender/preview/previewSchemaBuilder.ts'
import DynamicPreviewPageRenderEngine from '../../../../../components/dynamicPageRender/DynamicPreviewPageRenderEngine.jsx'

/**
 * Live preview panel.
 *
 * The important part here is that previewSchema is regenerated whenever
 * editorDraft changes.
 *
 * React state updates editorDraft on every input/change event.
 * Therefore:
 *
 * editor input
 *   -> editorDraft changes
 *   -> applyLiveEditToPageJson()
 *   -> NEW page JSON object
 *   -> DynamicPageRenderEngine receives NEW jsonSchema
 *   -> Formily schema is synchronously regenerated
 *   -> SchemaField receives a NEW key
 *   -> preview immediately reflects the new value
 */
export default function PagePreviewPanel({
  pageJson,
  editorMode,
  selectedComponentId,
  editorDraft,
}) {
  const previewSchema = useMemo(() => {
    if (!pageJson) {
      return null
    }

    /*
     * ADD mode does not have a persisted component id yet.
     *
     * We still return a new cloned schema so the preview pipeline remains
     * deterministic. The newly added component itself is not inserted until
     * it is persisted.
     */
    if (
      editorMode !== 'edit' ||
      selectedComponentId === null ||
      selectedComponentId === undefined
    ) {
      return pageJson
    }

    return applyLiveEditToPageJson(
      pageJson,
      selectedComponentId,
      editorDraft
    )
  }, [
    pageJson,
    editorMode,
    selectedComponentId,
    editorDraft,
  ])

  const hasComponents =
    Array.isArray(previewSchema?.components) &&
    previewSchema.components.length > 0

  /*
   * This key belongs to the preview container, not Formily itself.
   *
   * It guarantees that React sees the preview as changed even if a parent
   * accidentally reuses the same component tree.
   */
  const previewKey = useMemo(() => {
    if (!previewSchema) {
      return 'empty-preview'
    }

    try {
      return JSON.stringify(previewSchema)
    } catch {
      return 'preview'
    }
  }, [previewSchema])

  return (
    <div className="flex h-full flex-col rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
      <h2 className="mb-3 text-sm font-semibold uppercase tracking-[0.2em] text-slate-500">
        Live Page Preview
      </h2>

      <div className="min-h-0 flex-1 overflow-auto rounded-xl border border-dashed border-slate-200 bg-slate-50 p-4">
        {!previewSchema ? (
          <div className="flex h-full items-center justify-center text-sm text-slate-500">
            Loading preview…
          </div>
        ) : !hasComponents ? (
          <div className="flex h-full flex-col items-center justify-center gap-2 text-center text-sm text-slate-500">
            <p className="font-semibold text-slate-700">
              Nothing to preview yet
            </p>
            <p>
              Add a component from the palette to see it rendered here.
            </p>
          </div>
        ) : (
          <div key={previewKey}>
            <DynamicPreviewPageRenderEngine
              jsonSchema={previewSchema}
            />
          </div>
        )}
      </div>
    </div>
  )
}