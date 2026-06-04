import React from 'react'
import { NavLink } from 'react-router-dom'

const items = [
  { label: 'Overview',     to: '/admin_panel/overview'  },
  { label: 'Manage Pages', to: '/admin_panel/manage_page' },
  { label: 'Page JSON',    to: '/admin_panel/page_json' },
]

export default function Sidebar() {
  return (
    <aside className="border-b border-slate-200 bg-white lg:min-h-screen lg:border-b-0 lg:border-r">
      <div className="px-4 py-4 lg:px-5 lg:py-6">
        <div className="text-xs font-semibold uppercase tracking-[0.3em] text-slate-400">Admin</div>
        <p className="mt-1 text-sm font-semibold text-slate-900">Control panel</p>
      </div>

      <nav className="flex gap-1 overflow-x-auto px-3 pb-4 lg:flex-col lg:pb-4">
        {items.map((item) => (
          <NavLink
            key={item.label}
            to={item.to}
            className={({ isActive }) =>
              `flex items-center gap-2.5 whitespace-nowrap rounded-xl px-3 py-2 text-sm font-medium transition lg:whitespace-normal ${
                isActive
                  ? 'bg-slate-900 text-white shadow-sm'
                  : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
              }`
            }
          >
            <span className="text-base leading-none">{item.icon}</span>
            {item.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  )
}