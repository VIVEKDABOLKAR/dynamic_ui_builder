import React from 'react'

import { Link } from 'react-router-dom'

const items = [
  { label: 'Overview', to: '/admin_panel' },
  { label: 'Manage pages', to: '/admin_panel/manage_page' },
]

export default function Sidebar() {
  return (
    <aside className="border-b border-slate-200 bg-white lg:min-h-screen lg:border-b-0 lg:border-r">
      <div className="px-4 py-4 lg:px-5 lg:py-6">
        <div className="text-xs font-semibold uppercase tracking-[0.3em] text-slate-400">
          Admin
        </div>
        <p className="mt-1 text-sm font-semibold text-slate-900">Control panel</p>
      </div>

      <nav className="flex gap-2 overflow-x-auto px-4 pb-4 lg:flex-col lg:px-3 lg:pb-0">
        {items.map((item) => (
          <Link
            key={item.to}
            to={item.to}
            className="whitespace-nowrap rounded-xl px-3 py-2 text-sm font-medium text-slate-600 transition hover:bg-slate-100 hover:text-slate-900 lg:whitespace-normal"
          >
            {item.label}
          </Link>
        ))}
      </nav>
    </aside>
  )
}