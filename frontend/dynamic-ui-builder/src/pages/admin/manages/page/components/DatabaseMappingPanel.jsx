import React from 'react'

export default function DatabaseMappingPanel({ value, onChange }) {
  const mappingType = value.mappingType || 'ENTITY'

  return (
    <details className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
      
      <summary className="cursor-pointer list-none text-sm font-semibold uppercase tracking-[0.2em] text-slate-500">
        Database Mapping
      </summary>

      <div className="mt-4 grid gap-4 sm:grid-cols-2">
        <div className="grid gap-2 sm:col-span-2">
          <label className="text-sm font-medium text-slate-700">Mapping Type</label>
          <select
            value={mappingType}
            onChange={(event) => onChange('mappingType', event.target.value)}
            className="rounded-xl border border-slate-200 bg-slate-50 px-3 py-2 text-sm text-slate-900 outline-none transition focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
          >
            <option value="ENTITY">Entity</option>
            <option value="API">API</option>
          </select>
        </div>

        {mappingType === 'API' ? (
          <>
            <div className="grid gap-2 sm:col-span-2">
              <label className="text-sm font-medium text-slate-700">API Source</label>
              <input
                value={value.source || ''}
                onChange={(event) => onChange('source', event.target.value)}
                placeholder="Registry key (e.g. PAGE_LIST_API) or full URL"
                className="rounded-xl border border-slate-200 bg-slate-50 px-3 py-2 text-sm text-slate-900 outline-none transition focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
              />
            </div>
            <div className="grid gap-2 sm:col-span-2">
              <label className="text-sm font-medium text-slate-700">Response Path (optional)</label>
              <input
                value={value.responsePath || ''}
                onChange={(event) => onChange('responsePath', event.target.value)}
                placeholder="e.g. data.items"
                className="rounded-xl border border-slate-200 bg-slate-50 px-3 py-2 text-sm text-slate-900 outline-none transition focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
              />
            </div>
          </>
        ) : (
          <>
        <div className="grid gap-2">
          <label className="text-sm font-medium text-slate-700">Table Name</label>
          <input
            value={value.tableName}
            onChange={(event) => onChange('tableName', event.target.value)}
            placeholder="e.g. employee"
            className="rounded-xl border border-slate-200 bg-slate-50 px-3 py-2 text-sm text-slate-900 outline-none transition focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
          />
        </div>
        <div className="grid gap-2">
          <label className="text-sm font-medium text-slate-700">Column Name</label>
          <input
            value={value.columnName}
            onChange={(event) => onChange('columnName', event.target.value)}
            placeholder="e.g. country"
            className="rounded-xl border border-slate-200 bg-slate-50 px-3 py-2 text-sm text-slate-900 outline-none transition focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
          />
        </div>
        <div className="grid gap-2">
          <label className="text-sm font-medium text-slate-700">Attribute Name</label>
          <input
            value={value.attributeName}
            onChange={(event) => onChange('attributeName', event.target.value)}
            placeholder="e.g. country"
            className="rounded-xl border border-slate-200 bg-slate-50 px-3 py-2 text-sm text-slate-900 outline-none transition focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
          />
        </div>
        <div className="grid gap-2">
          <label className="text-sm font-medium text-slate-700">Display Name</label>
          <input
            value={value.displayName}
            onChange={(event) => onChange('displayName', event.target.value)}
            placeholder="e.g. Country"
            className="rounded-xl border border-slate-200 bg-slate-50 px-3 py-2 text-sm text-slate-900 outline-none transition focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
          />
        </div>
        <label className="inline-flex items-center gap-2 text-sm text-slate-700">
          <input
            type="checkbox"
            checked={value.isRequired}
            onChange={(event) => onChange('isRequired', event.target.checked)}
            className="h-4 w-4 rounded border-slate-300 text-cyan-500"
          />
          Required in mapping
        </label>
        <label className="inline-flex items-center gap-2 text-sm text-slate-700">
          <input
            type="checkbox"
            checked={value.isFilterable}
            onChange={(event) => onChange('isFilterable', event.target.checked)}
            className="h-4 w-4 rounded border-slate-300 text-cyan-500"
          />
          Filterable
        </label>
          </>
        )}
      </div>
    </details>
  )
}