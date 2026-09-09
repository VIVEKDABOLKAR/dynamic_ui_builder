import React from 'react'
import AdditionalPropertiesPanel from './AdditionalPropertiesPanel'
import DatabaseMappingPanel from './DatabaseMappingPanel'
import { AVAILABLE_COMPONENTS } from './ComponentPalette'

/**
 * Bottom panel: the existing Add/Edit Component form, unchanged in
 * behavior, now driven by `editorDraft` (from the controller) instead of
 * local state, so every keystroke here also updates the live preview.
 */
export default function ComponentEditorPanel({
  mode,
  editorDraft,
  onFieldChange,
  onAdditionalPropertyChange,
  onMappingValueChange,
  parentSearch,
  onParentSearchChange,
  parentOptions,
  newLookupValue,
  onNewLookupValueChange,
  onAddLookupValue,
  onRemoveLookupValue,
  errorMessage,
  saving,
  onSubmit,
  onCancel,
}) {
  const isTableComponent = editorDraft.componentType === 'table'
  const showLookupSection = editorDraft.componentType === 'select' || editorDraft.componentType === 'radio'

  return (
    <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
      <h2 className="mb-4 text-sm font-semibold uppercase tracking-[0.2em] text-slate-500">
        {mode === 'edit' ? 'Edit Component' : 'Add Component'}
      </h2>
      <form onSubmit={onSubmit} className="grid gap-6">
        {errorMessage && (
          <div className="rounded-xl bg-red-50 border border-red-200 p-4 text-sm text-red-700">
            {errorMessage}
          </div>
        )}

        <div className="grid gap-4 border-b border-slate-200 pb-4 lg:grid-cols-2">
          <div className="grid gap-2">
            <label className="text-sm font-medium text-slate-700">Component Name *</label>
            <input
              required
              name="componentName"
              value={editorDraft.componentName}
              onChange={onFieldChange}
              className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
            />
          </div>

          <div className="grid gap-2">
            <label className="text-sm font-medium text-slate-700">Component Type</label>
            <select
              name="componentType"
              value={editorDraft.componentType}
              onChange={onFieldChange}
              className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
            >
              {AVAILABLE_COMPONENTS.map((item) => (
                <option key={item.type} value={item.type}>{item.label}</option>
              ))}
            </select>
          </div>

          <div className="grid gap-2">
            <label className="text-sm font-medium text-slate-700">
              {isTableComponent ? 'Table Title *' : 'Label *'}
            </label>
            <input
              required
              name="labelName"
              value={editorDraft.labelName}
              onChange={onFieldChange}
              className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
            />
          </div>
          <div className="grid gap-2">
            <label className="text-sm font-medium text-slate-700">Placeholder</label>
            <input
              name="placeholder"
              value={editorDraft.placeholder}
              onChange={onFieldChange}
              className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
            />
          </div>

          <div className="grid gap-2">
            <label className="text-sm font-medium text-slate-700">Width *</label>
            <input
              required
              name="width"
              value={editorDraft.width}
              onChange={onFieldChange}
              placeholder="e.g. 200px or 50%"
              className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
            />
          </div>
          <div className="grid gap-2">
            <label className="text-sm font-medium text-slate-700">Sequence *</label>
            <input
              required
              type="number"
              name="sequenceNo"
              value={editorDraft.sequenceNo}
              onChange={onFieldChange}
              min={1}
              className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
            />
          </div>

          <div className="flex gap-4 lg:col-span-2">
            {['isRequired', 'isVisible', 'isDisabled'].map((field) => (
              <label key={field} className="inline-flex items-center gap-2 text-sm text-slate-700">
                <input
                  type="checkbox"
                  name={field}
                  checked={editorDraft[field]}
                  onChange={onFieldChange}
                  className="h-4 w-4 rounded border-slate-300 text-cyan-500"
                />
                {field.replace('is', '')}
              </label>
            ))}
          </div>

          <div className="space-y-3 rounded-xl border border-slate-200 bg-slate-50 p-4 lg:col-span-2">
            <label className="inline-flex items-center gap-2 text-sm font-medium text-slate-700">
              <input
                type="checkbox"
                name="isChildComponent"
                checked={editorDraft.isChildComponent}
                onChange={onFieldChange}
                className="h-4 w-4 rounded border-slate-300 text-cyan-500"
              />
              Add as child component
            </label>
            {editorDraft.isChildComponent && (
              <div className="grid gap-3">
                <input
                  value={parentSearch}
                  onChange={(e) => onParentSearchChange(e.target.value)}
                  placeholder="Search parent by id or name"
                  className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                />
                <select
                  name="parentComponentId"
                  value={editorDraft.parentComponentId}
                  onChange={onFieldChange}
                  className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                >
                  <option value="">Select parent component</option>
                  {parentOptions.map((item) => (
                    <option key={item.id} value={item.id}>
                      {item.id} - {item.componentName} ({item.componentType})
                    </option>
                  ))}
                </select>
                {parentOptions.length === 0 && (
                  <p className="text-xs text-amber-600">No card/layout components found for this page.</p>
                )}
              </div>
            )}
          </div>
        </div>

        <AdditionalPropertiesPanel value={editorDraft.additionalProperties} onChange={onAdditionalPropertyChange} />
        <DatabaseMappingPanel value={editorDraft.mappingValues} onChange={onMappingValueChange} />

        {showLookupSection && (
          <div className="space-y-4 rounded-2xl border border-slate-200 bg-slate-50 p-4">
            <h3 className="text-sm font-semibold text-slate-900">Lookup Values</h3>
            <div className="flex gap-2">
              <input
                type="text"
                value={newLookupValue}
                onChange={(e) => onNewLookupValueChange(e.target.value)}
                placeholder="Enter value"
                className="flex-1 rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                onKeyDown={(e) => { if (e.key === 'Enter') { e.preventDefault(); onAddLookupValue() } }}
              />
              <button
                type="button"
                onClick={onAddLookupValue}
                className="inline-flex items-center rounded-xl bg-slate-100 px-4 py-2 text-sm font-semibold text-slate-900 hover:bg-slate-200"
              >
                Add
              </button>
            </div>
            {editorDraft.lookupValues.length > 0 && (
              <div className="space-y-2">
                {editorDraft.lookupValues.map((item, idx) => (
                  <div key={item.id} className="flex items-center justify-between rounded-xl border border-slate-200 bg-white p-3">
                    <div className="flex items-center gap-3">
                      <span className="text-xs font-semibold text-slate-500">{idx + 1}</span>
                      <span className="text-sm text-slate-900">{item.lookupValue}</span>
                    </div>
                    <button
                      type="button"
                      onClick={() => onRemoveLookupValue(item.id)}
                      className="text-xs font-semibold text-red-500 hover:text-red-700"
                    >
                      Remove
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        <div className="flex gap-2">
          <button
            type="submit"
            disabled={saving}
            className="inline-flex items-center justify-center rounded-full bg-cyan-500 px-5 py-2 text-sm font-semibold text-white hover:bg-cyan-400 disabled:opacity-60"
          >
            {saving
              ? mode === 'edit' ? 'Updating…' : 'Adding…'
              : mode === 'edit' ? 'Update Component' : 'Add Component'}
          </button>
          {mode === 'edit' && (
            <button
              type="button"
              onClick={onCancel}
              className="inline-flex items-center justify-center rounded-full bg-slate-200 px-5 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-300"
            >
              Cancel Edit
            </button>
          )}
        </div>
      </form>
    </div>
  )
}
