import React, { useEffect, useState,useCallback } from 'react';x
import { useNavigate } from 'react-router-dom';
import { getAllUiPages } from '../../api/uiPageApi';
import { getUsername, logout } from '../../api/authApi';
import usePageUpdates from '../../hooks/usePageUpdates';
import { toast } from 'react-toastify';
export default function ViewerHome() {
  const navigate = useNavigate()
  const [pages, setPages] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [search, setSearch] = useState('')
  const handlePageUpdate = useCallback((update) => {
    console.log('WebSocket update received:', update)

    if (update.action === 'CREATED') {
      // Add the new page to the list
      setPages((prev) => [
        ...prev,
        {
          pageCode:    update.pageCode,
          pageName:    update.pageName,
          description: update.description || '',
          updatedAt:   update.updatedAt,
          isActive:    true,
        },
      ])
       toast.info(`"${update.pageName}" was updated`)
    }
  }, [])  


  const username = getUsername()

  useEffect(() => {
    const fetchPages = async () => {
      try {
        const data = await getAllUiPages()
        setPages(data)
      } catch (err) {
        setError('Failed to load pages. Please try again.')
      } finally {
        setLoading(false)
      }
    }
    fetchPages()
  }, [])


  const filtered = pages.filter((p) =>
    p.pageName.toLowerCase().includes(search.toLowerCase()) ||
    p.pageCode.toLowerCase().includes(search.toLowerCase()) ||
    (p.description || '').toLowerCase().includes(search.toLowerCase())
  )


usePageUpdates(handlePageUpdate)

  return (
    <main className="min-h-screen bg-slate-950 text-slate-100">


      <div className="mx-auto max-w-6xl px-6 py-10">

        {/* Header + search */}
        <div className="mb-8 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h2 className="text-2xl font-semibold text-white">All Pages</h2>
            <p className="mt-1 text-sm text-slate-400">
              {pages.length} page{pages.length !== 1 ? 's' : ''} available
            </p>
          </div>
        </div>

        {/* Loading */}
        {loading && (
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {[...Array(6)].map((_, i) => (
              <div
                key={i}
                className="h-40 animate-pulse rounded-2xl border border-white/5 bg-white/5"
              />
            ))}
          </div>
        )}

        {/* Error */}
        {error && (
          <div className="rounded-xl border border-red-500/30 bg-red-500/10 px-5 py-4 text-sm text-red-400">
            {error}
          </div>
        )}

        {/* Empty state */}
        {!loading && !error && filtered.length === 0 && (
          <div className="flex flex-col items-center justify-center py-24 text-center">
            <div className="mb-4 text-5xl">📄</div>
            <p className="text-lg font-medium text-slate-300">
              {search ? 'No pages match your search' : 'No pages available yet'}
            </p>
            <p className="mt-1 text-sm text-slate-500">
              {search ? 'Try a different keyword' : 'Check back later'}
            </p>
          </div>
        )}

        {/* Page cards */}
        {!loading && !error && filtered.length > 0 && (
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {filtered.map((page) => (
              <button
                key={page.pageCode}
                onClick={() => navigate(`/ui_demo/${page.pageCode}`)}
                className="group text-left rounded-2xl border border-white/10 bg-white/5 p-6 transition hover:border-cyan-400/40 hover:bg-white/10"
              >
                {/* Active badge */}
                <div className="mb-4 flex items-center justify-between">
                  <span className="rounded-full bg-cyan-400/10 px-3 py-1 text-xs font-medium text-cyan-300">
                    {page.pageCode}
                  </span>
                  <span className="flex items-center gap-1.5 text-xs text-emerald-400">
                    <span className="h-1.5 w-1.5 rounded-full bg-emerald-400" />
                    Active
                  </span>
                </div>

                {/* Page name */}
                <h3 className="text-base font-semibold text-white group-hover:text-cyan-300 transition-colors">
                  {page.pageName}
                </h3>

                {/* Description */}
                <p className="mt-1.5 text-sm text-slate-400 line-clamp-2">
                  {page.description || 'No description provided.'}
                </p>

                {/* Footer */}
                <div className="mt-4 flex items-center justify-between text-xs text-slate-500">
                  <span>
                    Updated {new Date(page.updatedAt).toLocaleDateString('en-IN', {
                      day: 'numeric', month: 'short', year: 'numeric'
                    })}
                  </span>
                  <span className="text-cyan-400 opacity-0 group-hover:opacity-100 transition-opacity">
                    Open →
                  </span>
                </div>
              </button>
            ))}
          </div>
        )}
      </div>
    </main>
  )
}