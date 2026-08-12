import React, { useState } from 'react'
import { NavLink, useLocation } from 'react-router-dom'

const items = [
  {
    icon: "◰",
    label: "Overview",
    to: "/admin_panel/overview",
  },
  {
    icon: "▤",
    label: "Pages",
    children: [
      {
        label: "Manage Pages",
        to: "/admin_panel/manage_page",
      },
      {
        label: "Page JSON",
        to: "/admin_panel/page_json",
      },
    ],
  },
  {
    icon: "~",
    label: "Global UI",
    children: [
      {
        label: "Navbar Config",
        to: "/admin_panel/global-ui/navbar",
      },
    ],
  },
  {
    icon: "⛭",
    label: "Facilities",
    children: [
      {
        label: "Approvals",
        to: "/admin_panel/facility-access",
      },
      {
        label: "Manage Facilities",
        to: "/admin_panel/manage-facilities",
      },
      {
        icon: '🔒',
        label: 'Route Access',
        to: '/admin_panel/route-access'
      },
    ],
  },
  {
    icon: "⇄",
    label: "Workflow",
    to: "/admin_panel/workflow-configuration",
  },
  {
    icon: "⛨",
    label: "Security",
    children: [
      {
        label: "Role Permissions",
        to: "/admin_panel/security-configuration",
      },
      // NEW: added for User Role Management feature
      {
        label: "User Role Management",
        to: "/admin_panel/user-role-management",
      },
    ],
  },
  {
    icon: "⌬",
    label: "Lookup's",
    to: "/admin_panel/lookup_managment",
  },
];

function SidebarItem({ item, pathname }) {
  const hasChildren = item.children?.length;

  const activeParent = hasChildren
    ? item.children.some((child) => pathname.startsWith(child.to))
    : pathname === item.to;

  const [open, setOpen] = useState(activeParent);

  if (!hasChildren) {
    return (
      <NavLink
        to={item.to}
        className={({ isActive }) =>
          `flex items-center gap-3 rounded-xl px-4 py-3 transition-all duration-200 ${isActive
            ? "bg-slate-900 text-white shadow"
            : "text-slate-600 hover:bg-slate-100 hover:text-slate-900"
          }`
        }
      >
        <span>{item.icon}</span>
        <span>{item.label}</span>
      </NavLink>
    );
  }

  return (
    <div>
      <button
        onClick={() => setOpen(!open)}
        className={`flex w-full items-center justify-between rounded-xl px-4 py-3 transition ${activeParent
            ? "bg-slate-100 text-slate-900"
            : "hover:bg-slate-50 text-slate-700"
          }`}
      >
        <div className="flex items-center gap-3">
          <span>{item.icon}</span>
          <span className="font-medium">{item.label}</span>
        </div>

        <span
          className={`transition-transform duration-300 ${open ? "rotate-90" : ""
            }`}
        >
          &gt;
        </span>
      </button>

      <div
        className={`overflow-hidden transition-all duration-300 ${open ? "max-h-96 mt-2" : "max-h-0"
          }`}
      >
        <div className="ml-6 border-l border-slate-200 pl-4 space-y-1">
          {item.children.map((child) => (
            <NavLink
              key={child.label}
              to={child.to}
              className={({ isActive }) =>
                `block rounded-lg px-3 py-2 text-sm transition ${isActive
                  ? "bg-slate-900 text-white"
                  : "text-slate-600 hover:bg-slate-100"
                }`
              }
            >
              {child.label}
            </NavLink>
          ))}
        </div>
      </div>
    </div>
  );
}


export default function Sidebar({ sidebarOpen, handleSidebarChange }) {
  const location = useLocation();
  if (!sidebarOpen) return null;

  return (
    <aside className="border-b border-slate-200 bg-white lg:min-h-screen lg:border-b-0 lg:border-r">
      <div className="px-4 py-4 lg:px-5 lg:py-6 flex justify-between">
        <div>
          <div className="text-xs font-semibold uppercase tracking-[0.3em] text-slate-400">Admin</div>
          <p className="mt-1 text-sm font-semibold text-slate-900">Control panel</p>
        </div>

        <button
          onClick={handleSidebarChange}
          className="rounded p-2 hover:bg-slate-200"
        >
          ✕
        </button>
      </div>

      <nav className="flex gap-1 overflow-x-auto px-3 pb-4 lg:flex-col lg:pb-4">
        {items.map((item) => (
          <SidebarItem
            key={item.label}
            item={item}
            pathname={location.pathname}
          />
        ))}
      </nav>


    </aside>
  )


}
