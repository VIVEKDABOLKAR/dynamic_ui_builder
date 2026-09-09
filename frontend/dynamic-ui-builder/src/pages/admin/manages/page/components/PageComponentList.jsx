import React from 'react'

/**
 * Right panel: compact list of components already on the page.
 */
export default function PageComponentList({
  components,
  selectedComponentId,
  onSelect,
  onOpenActions,
  onDelete,
  isLoading,
}) {
  const sorted = [...components].sort(
    (a, b) => (a.sequenceNo ?? 0) - (b.sequenceNo ?? 0)
  )

  return (
    <div className="flex h-full min-h-0 flex-col rounded-xl border border-slate-200 bg-white p-3 shadow-sm">
      {/* Header */}
      <div className="mb-2 shrink-0">
        <h2 className="text-xs font-semibold uppercase tracking-[0.15em] text-slate-500">
          Page Components
        </h2>
        <p className="mt-0.5 text-[11px] text-slate-400">
          Click a component to edit
        </p>
      </div>

      {/* List */}
      <div className="min-h-0 flex-1 overflow-y-auto pr-0.5">
        {isLoading ? (
          <div className="flex h-full items-center justify-center gap-2 text-xs text-slate-500">
            <span className="h-4 w-4 animate-spin rounded-full border-2 border-slate-300 border-t-cyan-500" />
            Loading...
          </div>
        ) : sorted.length === 0 ? (
          <p className="py-4 text-center text-xs text-slate-400">
            No components yet.
          </p>
        ) : (
          <div className="space-y-1.5">
            {sorted.map((component, idx) => {
              const isSelected = component.id === selectedComponentId

              return (
                <div
                  key={component.id}
                  role="button"
                  tabIndex={0}
                  onClick={() => onSelect(component)}
                  onKeyDown={(e) => {
                    if (e.key === 'Enter' || e.key === ' ') {
                      e.preventDefault()
                      onSelect(component)
                    }
                  }}
                  className={`group relative cursor-pointer rounded-lg border px-2.5 py-2 transition-colors ${
                    isSelected
                      ? 'border-cyan-400 bg-cyan-50'
                      : 'border-slate-200 bg-slate-50 hover:border-slate-300 hover:bg-slate-100'
                  }`}
                >
                  <div className="flex min-w-0 items-center gap-2">
                    {/* Number */}
                    <span className="shrink-0 text-[11px] font-medium text-slate-400">
                      {idx + 1}.
                    </span>

                    {/* Name + type */}
                    <div className="min-w-0 flex-1">
                      <p className="truncate text-xs font-semibold leading-4 text-slate-800">
                        {component.componentName || '(unnamed)'}
                      </p>

                      <p className="truncate text-[9px] font-medium uppercase leading-3 tracking-wider text-slate-400">
                        {component.componentType}
                      </p>
                    </div>

                    {/* Actions */}
                    <div
                      className="flex shrink-0 items-center gap-1 opacity-0 transition-opacity group-hover:opacity-100 group-focus-within:opacity-100"
                      onClick={(e) => e.stopPropagation()}
                    >
                      <button
                        type="button"
                        onClick={() => onOpenActions(component)}
                        className="rounded px-1.5 py-1 text-[10px] font-semibold text-blue-700 hover:bg-blue-100"
                        title="Actions"
                      >
                        ⋮
                      </button>

                      <button
                        type="button"
                        onClick={() => onDelete(component.id)}
                        className="rounded px-1.5 py-1 text-[10px] font-semibold text-red-600 hover:bg-red-100"
                        title="Delete"
                      >
                        ×
                      </button>
                    </div>
                  </div>
                </div>
              )
            })}
          </div>
        )}
      </div>
    </div>
  )
}