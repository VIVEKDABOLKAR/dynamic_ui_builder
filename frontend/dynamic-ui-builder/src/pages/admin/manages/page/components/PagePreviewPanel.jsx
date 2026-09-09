import React, { useMemo } from 'react'
import DynamicPageRenderEngine from '../../../../../components/dynamicPageRender/DynamicPageRenderEngine'
import { applyLiveEditToPageJson } from '../../../../../components/dynamicPageRender/preview/previewSchemaBuilder.ts'

/**
 * Center panel: renders the SAME assembled page JSON real pages use
 * (fetched once by the controller via getUiPageByCode), with the
 * component currently being edited patched in live from editorDraft.
 *
 * NOTE: click-to-select inside the rendered preview is intentionally not
 * implemented (would require touching the shared muiComponents renderer
 * used by real pages too). Selection is driven by PageComponentList.
 */
export default function PagePreviewPanel({ pageJson, editorMode, selectedComponentId, editorDraft }) {
  const previewSchema = useMemo(() => {
    if (!pageJson) return null
    if (editorMode !== 'edit' || !selectedComponentId) return pageJson
    const result = applyLiveEditToPageJson(pageJson, selectedComponentId, editorDraft)
    return result;
  }, [pageJson, editorMode, selectedComponentId, editorDraft])

  const hasComponents = Array.isArray(previewSchema?.components) && previewSchema.components.length > 0


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
            <p className="font-semibold text-slate-700">Nothing to preview yet</p>
            <p>Add a component from the palette to see it rendered here.</p>
          </div>
        ) : (
          <DynamicPageRenderEngine jsonSchema={previewSchema} />
        )}
      </div>
    </div>
  )
}
