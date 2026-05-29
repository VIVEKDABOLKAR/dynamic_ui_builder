import React from 'react'

export default function AdditionalPropertiesPanel({ value, onChange }) {
  return (
    <details className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
      <summary className="cursor-pointer list-none text-sm font-semibold uppercase tracking-[0.2em] text-slate-500">
        Additional Properties
      </summary>

      <div className="mt-4 grid gap-4 sm:grid-cols-2">
        <div className="grid gap-2">
          <label className="text-sm font-medium text-slate-700">Height</label>
          <input
            value={value.height}
            onChange={(event) => onChange('height', event.target.value)}
            placeholder="e.g. 48px"
            className="rounded-xl border border-slate-200 bg-slate-50 px-3 py-2 text-sm text-slate-900 outline-none transition focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
          />
        </div>
        <div className="grid gap-2">
          <label className="text-sm font-medium text-slate-700">Border</label>
          <input
            value={value.border}
            onChange={(event) => onChange('border', event.target.value)}
            placeholder="e.g. 1px solid #e2e8f0"
            className="rounded-xl border border-slate-200 bg-slate-50 px-3 py-2 text-sm text-slate-900 outline-none transition focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
          />
        </div>
        <div className="grid gap-2">
          <label className="text-sm font-medium text-slate-700">Border Radius</label>
          <input
            value={value.borderRadius}
            onChange={(event) => onChange('borderRadius', event.target.value)}
            placeholder="e.g. 12px"
            className="rounded-xl border border-slate-200 bg-slate-50 px-3 py-2 text-sm text-slate-900 outline-none transition focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
          />
        </div>
        <div className="grid gap-2">
          <label className="text-sm font-medium text-slate-700">Class Name</label>
          <input
            value={value.className}
            onChange={(event) => onChange('className', event.target.value)}
            placeholder="e.g. shadow-lg"
            className="rounded-xl border border-slate-200 bg-slate-50 px-3 py-2 text-sm text-slate-900 outline-none transition focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
          />
        </div>
      </div>
    </details>
  )
}