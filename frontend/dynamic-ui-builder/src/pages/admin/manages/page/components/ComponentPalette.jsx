import React from 'react'

export const AVAILABLE_COMPONENTS = [
  { type: 'input', label: 'Text Field', icon: '🔤' },
  { type: 'textarea', label: 'Text Area', icon: '📝' },
  { type: 'button', label: 'Button', icon: '🔘' },
  { type: 'select', label: 'Dropdown', icon: '🔽' },
  { type: 'radio', label: 'Radio Button', icon: '⭕' },
  { type: 'checkbox', label: 'Checkbox', icon: '☑️' },
  { type: 'table', label: 'Data Table', icon: '📊' },
  { type: 'card', label: 'Card', icon: '🗂️' },
  { type: 'datepicker', label: 'DatePicker', icon: '📅' },
  { type: 'layout', label: 'Layout', icon: '📐' },
]

export default function ComponentPalette({ activeType, onSelect }) {
  return (
    <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
      <h2 className="mb-3 text-sm font-semibold uppercase tracking-[0.2em] text-slate-500">
        Available Components
      </h2>
      <div className="space-y-3">
        {AVAILABLE_COMPONENTS.map((item) => (
          <div
            key={item.type}
            onClick={() => onSelect(item)}
            role="button"
            tabIndex={0}
            className={`rounded-2xl border p-4 bg-slate-50 hover:shadow-md transition-colors cursor-pointer ${
              activeType === item.type ? 'border-cyan-400 bg-cyan-50' : 'border-slate-200'
            }`}
          >
            <div className="flex items-center gap-3">
              <div className="flex h-10 w-10 items-center justify-center rounded-2xl bg-cyan-500 text-xl text-white shadow-sm">
                {item.icon}
              </div>
              <div>
                <p className="font-semibold text-slate-900">{item.label}</p>
                <p className="text-xs uppercase tracking-[0.18em] text-slate-400">type: {item.type}</p>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
