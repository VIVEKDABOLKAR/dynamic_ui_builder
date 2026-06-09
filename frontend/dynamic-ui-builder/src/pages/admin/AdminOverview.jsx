import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getAllPages } from '../../api/adminPageApi'
import { getComponentsByPage } from '../../api/componentApi'
import { getActionsByPageCode } from '../../api/actionsPageApi'

function StatCard({ icon, label, value, sub, accent }) {
  const accents = {
    cyan: { bg: 'bg-cyan-50', ring: 'ring-cyan-100', icon: 'bg-cyan-500', text: 'text-cyan-600' },
    slate: { bg: 'bg-slate-50', ring: 'ring-slate-100', icon: 'bg-slate-700', text: 'text-slate-600' },
    emerald: { bg: 'bg-emerald-50', ring: 'ring-emerald-100', icon: 'bg-emerald-500', text: 'text-emerald-600' },
    amber: { bg: 'bg-amber-50', ring: 'ring-amber-100', icon: 'bg-amber-500', text: 'text-amber-600' },
  }
  const c = accents[accent] ?? accents.slate
  return (
    <div className={`rounded-2xl border border-slate-200 ${c.bg} ring-1 ${c.ring} p-5 shadow-sm`}>
      <div className="flex items-start justify-between">
        <div className={`flex h-10 w-10 items-center justify-center rounded-xl ${c.icon} text-lg text-white shadow-sm`}>
          {icon}
        </div>
        <span className={`text-xs font-semibold uppercase tracking-widest ${c.text}`}>{label}</span>
      </div>
      <p className="mt-4 text-3xl font-bold tabular-nums text-slate-900">{value}</p>
      {sub && <p className="mt-1 text-xs text-slate-500">{sub}</p>}
    </div>
  )
}

function QuickLink({ to, icon, title, desc, badge }) {
  return (
    <Link
      to={to}
      className="group relative flex flex-col gap-3 rounded-2xl border border-slate-200 bg-white p-5 shadow-sm transition-all hover:-translate-y-0.5 hover:border-cyan-300 hover:shadow-md"
    >
      {badge && (
        <span className="absolute right-4 top-4 rounded-full bg-cyan-100 px-2 py-0.5 text-[10px] font-bold uppercase tracking-widest text-cyan-700">
          {badge}
        </span>
      )}
      <div className="flex h-11 w-11 items-center justify-center rounded-2xl bg-slate-900 text-xl text-white shadow-sm transition group-hover:bg-cyan-500">
        {icon}
      </div>
      <div>
        <p className="font-semibold text-slate-900">{title}</p>
        <p className="mt-0.5 text-sm text-slate-500">{desc}</p>
      </div>
      <span className="mt-auto text-xs font-semibold text-cyan-500 opacity-0 transition group-hover:opacity-100">
        Open →
      </span>
    </Link>
  )
}

function PageRow({ page }) {
  return (
    <div className="flex items-center justify-between rounded-xl border border-slate-100 bg-slate-50 px-4 py-3 transition hover:border-slate-200 hover:bg-white">
      <div className="flex items-center gap-3 min-w-0">
        <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-slate-200 text-sm font-bold text-slate-600">
          {(page.pageName ?? 'P')[0].toUpperCase()}
        </div>
        <div className="min-w-0">
          <p className="truncate text-sm font-semibold text-slate-800">{page.pageName}</p>
          <p className="font-mono text-[11px] text-slate-400">{page.pageCode}</p>
        </div>
      </div>
      <div className="flex shrink-0 items-center gap-2">
        <span className={`rounded-full px-2 py-0.5 text-[10px] font-bold uppercase tracking-wide ${page.isActive ? 'bg-emerald-100 text-emerald-600' : 'bg-slate-200 text-slate-500'
          }`}>
          {page.isActive ? 'Active' : 'Off'}
        </span>
        <Link
          to={`/admin_panel/manage_page/${page.pageCode}/components`}
          className="rounded-lg bg-slate-100 px-2 py-1 text-[11px] font-semibold text-slate-600 transition hover:bg-cyan-100 hover:text-cyan-700"
        >
          Edit
        </Link>
      </div>
    </div>
  )
}

export default function AdminOverview() {
  const [pages, setPages] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    getAllPages()
      .then((data) =>setPages(Array.isArray(data) ? data : []))
      .catch(() => setPages([]))
      .finally(() => setLoading(false))
  }, [])
  
  useEffect(() => {
  if (pages.length > 0) {
         loadTotalComponents();
         loadTotalActions();
    }
  },[pages])

  const [components, setComponents] = useState([]);
  const [totalComponents, setTotalComponents] = useState(0);

  const loadTotalComponents = async () => {
    let count = 0;

    for (const page of pages) {
      const components = await getComponentsByPage(page.pageCode);
      count += components.length;
    }

    setTotalComponents(count);
  };



  const [actions,setActions] = useState([]);
  const [totalActions,setTotalActions] = useState(0);

  const loadTotalActions = async () => {
    let count = 0;
    for (const page of pages){
          const actions = await getActionsByPageCode(page.pageCode);
          count += actions.length;
    }
    setTotalActions(count);
  }

  
  
  const activePages = pages.filter((p) => p.isActive)

  return (
    <>
      <style>{`
        @keyframes slideUp { from { opacity:0; transform:translateY(16px); } to { opacity:1; transform:translateY(0); } }
        .slide-up  { animation: slideUp .3s ease both; }
        .slide-up-2 { animation: slideUp .3s .06s ease both; }
        .slide-up-3 { animation: slideUp .3s .12s ease both; }
        .slide-up-4 { animation: slideUp .3s .18s ease both; }
      `}</style>

      <div className="space-y-8">
        <div className="slide-up flex flex-col gap-1">
          <h1 className="text-2xl font-bold text-slate-900">Admin Overview</h1>
          <p className="text-sm text-slate-500">
            Monitor your dynamic UI builder — pages, components, actions, and schemas at a glance.
          </p>
        </div>

        <div className="slide-up-2 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          <StatCard icon="📄" label="Total Pages" value={loading ? '—' : pages.length} sub="pages configured" accent="slate" />
          <StatCard icon="✅" label="Active Pages" value={loading ? '—' : activePages.length} sub={`${pages.length - activePages.length} inactive`} accent="emerald" />
          <StatCard icon="🧩" label="Components"   value={loading ? "—" : totalComponents} sub="across all pages" accent="cyan" />
          <StatCard icon="⚡" label="Actions" value={loading ? "—" : totalActions}  sub="configured globally" accent="amber" />
        </div>

        <div className="slide-up-3">
          <h2 className="mb-4 text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">Quick Access</h2>
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
            <QuickLink to="/admin_panel/manage_page" icon="📄" title="Manage Pages" desc="Create, edit and delete UI pages" />
            <QuickLink to="/admin_panel/manage_page/add" icon="➕" title="New Page" desc="Configure a new page from scratch" />
            <QuickLink to="/admin_panel/page_json" icon="{ }" title="Page JSON" desc="Inspect the generated JSON schema per page" />
            <QuickLink to="/ui_demo/10010" icon="🖥️" title="Preview UI" desc="See the dynamic rendering engine in action" />
          </div>
        </div>

        {/* <div className="slide-up-4 grid gap-6 lg:grid-cols-[1fr_300px]">
          <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
            <div className="mb-4 flex items-center justify-between">
              <h2 className="text-sm font-semibold uppercase tracking-[0.2em] text-slate-500">Recent Pages</h2>
              <Link to="/admin_panel/manage_page" className="text-xs font-semibold text-cyan-500 transition hover:text-cyan-400">
                View all →
              </Link>
            </div>
            {loading ? (
              <div className="flex items-center justify-center gap-2 py-10 text-sm text-slate-400">
                <span className="h-4 w-4 animate-spin rounded-full border-2 border-slate-200 border-t-cyan-400" />
                Loading…
              </div>
            ) : recentPages.length === 0 ? (
              <div className="flex flex-col items-center justify-center gap-2 py-10 text-sm text-slate-400">
                <p className="font-medium text-slate-600">No pages yet</p>
                <Link to="/admin_panel/manage_page/add" className="inline-flex items-center rounded-full bg-cyan-500 px-4 py-1.5 text-xs font-semibold text-white hover:bg-cyan-400">
                  + Create first page
                </Link>
              </div>
            ) : (
              <div className="space-y-2">
                {recentPages.map((page) => <PageRow key={page.pageCode} page={page} />)}
              </div>
            )}
          </div>

          <div className="flex flex-col gap-4">
            <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
              <h2 className="mb-3 text-sm font-semibold uppercase tracking-[0.2em] text-slate-500">How It Works</h2>
              <ol className="space-y-3">
                {[
                  { step: '1', text: 'Create a Page with a unique code and name.' },
                  { step: '2', text: 'Add Components — inputs, buttons, tables, cards…' },
                  { step: '3', text: 'Attach Actions like Submit Form or Navigate.' },
                  { step: '4', text: 'Preview the JSON schema & render it dynamically.' },
                ].map(({ step, text }) => (
                  <li key={step} className="flex items-start gap-3">
                    <span className="flex h-5 w-5 shrink-0 items-center justify-center rounded-full bg-slate-900 text-[10px] font-bold text-white">{step}</span>
                    <p className="text-sm text-slate-600 leading-snug">{text}</p>
                  </li>
                ))}
              </ol>
            </div>
            <div className="rounded-2xl border border-cyan-200 bg-cyan-50 p-5">
              <p className="text-xs font-semibold uppercase tracking-widest text-cyan-600 mb-2">Tip</p>
              <p className="text-sm text-cyan-800 leading-relaxed">
                Use the <strong>Page JSON</strong> viewer to verify your schema before integrating it with the render engine.
              </p>
              <Link to="/admin_panel/page_json" className="mt-3 inline-flex items-center rounded-full bg-cyan-500 px-3 py-1.5 text-xs font-semibold text-white transition hover:bg-cyan-400">
                Open JSON viewer →
              </Link>
            </div>
          </div>
        </div> */}
      </div>
    </>
  )
}