import React, { useCallback, useEffect, useRef, useState } from 'react'
import { getAllPages } from '../../../api/adminPageApi'
import { getUiPageByCode } from '../../../api/uiPageApi'

/* ─── tiny helpers ─────────────────────────────────────────────── */
function prettyJson(raw) {
  try {
    return JSON.stringify(typeof raw === 'string' ? JSON.parse(raw) : raw, null, 2)
  } catch {
    return typeof raw === 'string' ? raw : JSON.stringify(raw, null, 2)
  }
}

function copyToClipboard(text) {
  navigator.clipboard?.writeText(text).catch(() => {
    const ta = document.createElement('textarea')
    ta.value = text
    document.body.appendChild(ta)
    ta.select()
    document.execCommand('copy')
    document.body.removeChild(ta)
  })
}

/* ─── syntax highlighter (no dependency) ───────────────────────── */
function highlight(json) {
  return json
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(
      /("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g,
      (match) => {
        let cls = 'json-num'
        if (/^"/.test(match)) {
          cls = /:$/.test(match) ? 'json-key' : 'json-str'
        } else if (/true|false/.test(match)) {
          cls = 'json-bool'
        } else if (/null/.test(match)) {
          cls = 'json-null'
        }
        return `<span class="${cls}">${match}</span>`
      },
    )
}

/* ─── component ─────────────────────────────────────────────────── */
export default function PageJson() {
  const [pages, setPages] = useState([])
  const [selectedPage, setSelectedPage] = useState(null)
  const [jsonData, setJsonData] = useState(null)
  const [loadingPages, setLoadingPages] = useState(true)
  const [loadingJson, setLoadingJson] = useState(false)
  const [search, setSearch] = useState('')
  const [copied, setCopied] = useState(false)
  const [viewMode, setViewMode] = useState('pretty')
  const preRef = useRef(null)

  useEffect(() => {
    setLoadingPages(true)
    getAllPages()
      .then((data) => setPages(Array.isArray(data) ? data : []))
      .catch(() => setPages([]))
      .finally(() => setLoadingPages(false))
  }, [])

  const selectPage = useCallback(async (page) => {
    setSelectedPage(page)
    setJsonData(null)
    setLoadingJson(true)
    try {
      const data = await getUiPageByCode(page.pageCode)
      setJsonData(data)
    } catch {
      setJsonData({ error: 'Failed to load schema for this page.' })
    } finally {
      setLoadingJson(false)
    }
  }, [])

  const handleCopy = () => {
    const text = prettyJson(jsonData?.jsonSchema ?? jsonData)
    copyToClipboard(text)
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }

  const handleDownload = () => {
    const text = prettyJson(jsonData?.jsonSchema ?? jsonData)
    const blob = new Blob([text], { type: 'application/json' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `${selectedPage?.pageCode ?? 'schema'}.json`
    a.click()
    URL.revokeObjectURL(url)
  }

  const filteredPages = pages.filter((p) => {
    const q = search.toLowerCase()
    return (
      (p.pageName ?? '').toLowerCase().includes(q) ||
      (p.pageCode ?? '').toLowerCase().includes(q)
    )
  })

  const rawJson = jsonData ? prettyJson(jsonData?.jsonSchema ?? jsonData) : ''
  const highlighted = rawJson ? highlight(rawJson) : ''

  return (
    <>
      <style>{`
        .json-key  { color: #0369a1; }
        .json-str  { color: #15803d; }
        .json-num  { color: #b45309; }
        .json-bool { color: #7c3aed; font-weight: 600; }
        .json-null { color: #dc2626; }
        .page-item { transition: background .15s, border-color .15s; }
        .page-item:hover { background: #f8fafc; }
        .page-item.active { background: #ecfeff; border-color: #22d3ee; }
        .sidebar-scroll::-webkit-scrollbar { width: 4px; }
        .sidebar-scroll::-webkit-scrollbar-track { background: transparent; }
        .sidebar-scroll::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 9999px; }
        .json-scroll::-webkit-scrollbar { width: 6px; height: 6px; }
        .json-scroll::-webkit-scrollbar-track { background: #f1f5f9; }
        .json-scroll::-webkit-scrollbar-thumb { background: #94a3b8; border-radius: 9999px; }
        @keyframes fadeIn { from { opacity:0; transform:translateY(6px);} to {opacity:1;transform:translateY(0);} }
        .fade-in { animation: fadeIn .25s ease both; }
      `}</style>

      <div className="flex h-full flex-col gap-0 -m-4 sm:-m-4 lg:-m-4" style={{ minHeight: 'calc(100vh - 112px)' }}>

        {/* top bar */}
        <div className="flex flex-col gap-3 border-b border-slate-200 bg-white px-6 py-4 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h1 className="text-xl font-semibold text-slate-900">Page JSON Schemas</h1>
            <p className="text-sm text-slate-500">Select a page to inspect its generated JSON schema.</p>
          </div>
          {selectedPage && (
            <div className="flex items-center gap-2">
              <span className="inline-flex items-center rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold text-slate-600">
                {selectedPage.pageCode}
              </span>
              {/* <button
                onClick={handleCopy}
                className="inline-flex items-center gap-1.5 rounded-full border border-slate-200 bg-white px-3 py-1.5 text-xs font-semibold text-slate-700 shadow-sm transition hover:border-cyan-300 hover:bg-cyan-50 hover:text-cyan-700"
              >
                {copied ? '✓ Copied!' : '⎘ Copy'}
              </button>
              <button
                onClick={handleDownload}
                className="inline-flex items-center gap-1.5 rounded-full bg-slate-900 px-3 py-1.5 text-xs font-semibold text-white shadow-sm transition hover:bg-slate-700"
              >
                ↓ Download
              </button> */}
            </div>
          )}
        </div>

        {/* main split */}
        <div className="flex flex-1 overflow-hidden" style={{ minHeight: 0 }}>

          {/* sidebar */}
          <aside className="flex w-64 shrink-0 flex-col border-r border-slate-200 bg-white">
            <div className="border-b border-slate-100 px-3 py-3">
              <input
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                placeholder="Search pages…"
                className="w-full rounded-xl border border-slate-200 bg-slate-50 px-3 py-2 text-sm outline-none transition placeholder:text-slate-400 focus:border-cyan-400 focus:bg-white focus:ring-2 focus:ring-cyan-100"
              />
            </div>

            <div className="sidebar-scroll flex-1 overflow-y-auto px-2 py-2">
              {loadingPages ? (
                <div className="flex items-center justify-center gap-2 py-10 text-sm text-slate-400">
                  <span className="h-4 w-4 animate-spin rounded-full border-2 border-slate-200 border-t-cyan-400" />
                  Loading…
                </div>
              ) : filteredPages.length === 0 ? (
                <p className="px-3 py-6 text-center text-sm text-slate-400">No pages found.</p>
              ) : (
                filteredPages.map((page) => (
                  <button
                    key={page.pageCode}
                    onClick={() => selectPage(page)}
                    className={`page-item w-full rounded-xl border px-3 py-2.5 text-left mb-1 ${
                      selectedPage?.pageCode === page.pageCode
                        ? 'active border-cyan-200'
                        : 'border-transparent'
                    }`}
                  >
                    <p className="text-sm font-semibold text-slate-800 leading-tight">{page.pageName}</p>
                    <p className="mt-0.5 font-mono text-[11px] text-slate-400">{page.pageCode}</p>
                    {page.isActive !== undefined && (
                      <span className={`mt-1 inline-block rounded-full px-1.5 py-0.5 text-[10px] font-semibold ${
                        page.isActive ? 'bg-emerald-50 text-emerald-600' : 'bg-slate-100 text-slate-400'
                      }`}>
                        {page.isActive ? 'Active' : 'Inactive'}
                      </span>
                    )}
                  </button>
                ))
              )}
            </div>

            <div className="border-t border-slate-100 px-3 py-2">
              <p className="text-[11px] text-slate-400">{filteredPages.length} page{filteredPages.length !== 1 ? 's' : ''}</p>
            </div>
          </aside>

          {/* json panel */}
          <main className="flex flex-1 flex-col overflow-hidden bg-slate-50">
            {!selectedPage ? (
              <div className="flex flex-1 flex-col items-center justify-center gap-3 text-slate-400">
                <div className="flex h-16 w-16 items-center justify-center rounded-2xl border-2 border-dashed border-slate-200 text-3xl">
                  {'{ }'}
                </div>
                <p className="text-sm font-medium">Select a page from the sidebar</p>
                <p className="text-xs">Its JSON schema will appear here</p>
              </div>
            ) : loadingJson ? (
              <div className="flex flex-1 items-center justify-center gap-3 text-sm text-slate-400">
                <span className="h-5 w-5 animate-spin rounded-full border-2 border-slate-200 border-t-cyan-500" />
                Loading schema…
              </div>
            ) : (
              <div className="fade-in flex flex-1 flex-col overflow-hidden">
                {/* <div className="flex items-center justify-between border-b border-slate-200 bg-white px-4 py-2">
                  <div className="flex items-center gap-1 rounded-lg border border-slate-200 p-0.5">
                    <button
                      onClick={() => setViewMode('pretty')}
                      className={`rounded-md px-3 py-1 text-xs font-semibold transition ${
                        viewMode === 'pretty' ? 'bg-slate-900 text-white' : 'text-slate-500 hover:text-slate-700'
                      }`}
                    >
                      Pretty
                    </button>
                    <button
                      onClick={() => setViewMode('raw')}
                      className={`rounded-md px-3 py-1 text-xs font-semibold transition ${
                        viewMode === 'raw' ? 'bg-slate-900 text-white' : 'text-slate-500 hover:text-slate-700'
                      }`}
                    >
                      Raw
                    </button>
                  </div>
                  <span className="font-mono text-[11px] text-slate-400">
                    {rawJson.length.toLocaleString()} chars · {rawJson.split('\n').length} lines
                  </span>
                </div> */}

                <div className="json-scroll flex-1 overflow-auto p-4">
                  {viewMode === 'pretty' ? (
                    <pre
                      ref={preRef}
                      className="min-h-full rounded-2xl border border-slate-200 bg-white p-5 font-mono text-[13px] leading-relaxed shadow-sm"
                      dangerouslySetInnerHTML={{ __html: highlighted }}
                    />
                  ) : (
                    <pre className="min-h-full rounded-2xl border border-slate-200 bg-white p-5 font-mono text-[13px] leading-relaxed text-slate-700 shadow-sm whitespace-pre-wrap break-all">
                      {rawJson}
                    </pre>
                  )}
                </div>
              </div>
            )}
          </main>
        </div>
      </div>
    </>
  )
}