import React, { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import toast from 'react-hot-toast'
import { FaEye, FaHistory, FaUndo } from 'react-icons/fa'
import { createVersion, getVersion, getVersionHistory, restoreVersion } from '../../../../../api/pageVersionApi'

export default function PageVersionHistory() {
  const { pageCode } = useParams()

  const [versions, setVersions] = useState([])
  const [isLoading, setIsLoading] = useState(false)
  const [creating, setCreating] = useState(false)
  const [changeSummary, setChangeSummary] = useState('')

  const [previewVersion, setPreviewVersion] = useState(null) // full DTO w/ jsonSchema
  const [previewLoading, setPreviewLoading] = useState(false)
  const [restoreTarget, setRestoreTarget] = useState(null) // versionNumber

  const loadHistory = async () => {
    setIsLoading(true)
    try {
      const res = await getVersionHistory(pageCode)
      setVersions(Array.isArray(res) ? res : [])
    } catch (error) {
      console.error('Failed to load version history', error)
      setVersions([])
    } finally {
      setIsLoading(false)
    }
  }

  useEffect(() => {
    loadHistory()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pageCode])

  const handleCreateVersion = async () => {
    setCreating(true)
    try {
      await createVersion(pageCode, changeSummary.trim() || null)
      toast.success('New version created')
      setChangeSummary('')
      await loadHistory()
    } catch (error) {
      console.error('Failed to create version', error)
    } finally {
      setCreating(false)
    }
  }

  const handlePreview = async (versionNumber) => {
    setPreviewLoading(true)
    try {
      const res = await getVersion(pageCode, versionNumber)
      setPreviewVersion(res)
    } catch (error) {
      console.error('Failed to load version', error)
    } finally {
      setPreviewLoading(false)
    }
  }

  const confirmRestore = async () => {
    if (!restoreTarget) return
    try {
      await restoreVersion(pageCode, restoreTarget)
      toast.success(`Version ${restoreTarget} restored to current working state`)
    } catch (error) {
      console.error('Failed to restore version', error)
    } finally {
      setRestoreTarget(null)
    }
  }

  const formatDate = (value) =>
    value
      ? new Date(value).toLocaleString(undefined, { dateStyle: 'medium', timeStyle: 'short' })
      : '-'

  return (
    <div className="space-y-4">
      <div className="flex flex-col gap-3 rounded-2xl border border-slate-200 bg-white p-4 shadow-sm sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="flex items-center gap-2 text-xl font-semibold text-slate-900">
            <FaHistory className="text-cyan-500" /> Version History
          </h1>
          <p className="text-sm text-slate-500">
            Page: <span className="font-medium text-slate-700">{pageCode}</span>. Editing the
            page never creates a version automatically — click "Create Version" below whenever
            the current working state should become permanent history.
          </p>
        </div>

        <Link
          to={`/admin_panel/manage_page`}
          className="inline-flex w-fit items-center rounded-full border border-slate-300 px-4 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-100"
        >
          Back to Manage Pages
        </Link>
      </div>

      <div className="flex flex-col gap-3 rounded-2xl border border-slate-200 bg-white p-4 shadow-sm sm:flex-row sm:items-center">
        <input
          type="text"
          value={changeSummary}
          onChange={(e) => setChangeSummary(e.target.value)}
          placeholder="Optional note about this version (e.g. 'Added approve button')"
          className="w-full flex-1 rounded-lg border border-slate-300 px-3 py-2 text-sm focus:border-cyan-400 focus:outline-none"
        />
        <button
          type="button"
          onClick={handleCreateVersion}
          disabled={creating}
          className="inline-flex w-fit items-center rounded-full bg-cyan-400 px-4 py-2 text-sm font-semibold text-slate-950 transition hover:bg-cyan-300 disabled:opacity-60"
        >
          {creating ? 'Creating...' : '+ Create Version'}
        </button>
      </div>

      <div className="rounded-2xl border border-slate-200 bg-white shadow-sm">
        <div className="border-b border-slate-100 px-4 py-3 sm:px-5">
          <h2 className="text-sm font-semibold uppercase tracking-[0.2em] text-slate-500">
            Versions
          </h2>
        </div>

        {isLoading ? (
          <div className="flex h-40 items-center justify-center gap-3 text-sm text-slate-500">
            <span className="h-5 w-5 animate-spin rounded-full border-2 border-slate-300 border-t-cyan-500" />
            Loading version history...
          </div>
        ) : versions.length === 0 ? (
          <div className="flex h-40 flex-col items-center justify-center gap-2 text-sm text-slate-500">
            <p className="font-semibold text-slate-700">No versions yet</p>
            <p>Click "Create Version" to snapshot the current working state.</p>
          </div>
        ) : (
          <div className="divide-y divide-slate-100">
            {versions.map((v) => (
              <div
                key={v.id}
                className="flex flex-col gap-2 px-4 py-3 sm:flex-row sm:items-center sm:justify-between sm:px-5"
              >
                <div>
                  <div className="flex items-center gap-2">
                    <span className="rounded-full bg-slate-900 px-3 py-1 text-xs font-semibold text-white">
                      v{v.versionNumber}
                    </span>
                    <span className="text-sm text-slate-500">{formatDate(v.createdAt)}</span>
                  </div>
                  {v.changeSummary && (
                    <p className="mt-1 text-sm text-slate-700">{v.changeSummary}</p>
                  )}
                </div>

                <div className="flex items-center gap-3">
                  <button
                    type="button"
                    onClick={() => handlePreview(v.versionNumber)}
                    title="Preview"
                    className="rounded-full bg-blue-500 p-2 text-white hover:bg-blue-400"
                  >
                    <FaEye size={14} />
                  </button>
                  <button
                    type="button"
                    onClick={() => setRestoreTarget(v.versionNumber)}
                    title="Restore this version into the current working state"
                    className="rounded-full bg-amber-500 p-2 text-white hover:bg-amber-400"
                  >
                    <FaUndo size={14} />
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Preview modal */}
      {(previewVersion || previewLoading) && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4"
          onClick={() => setPreviewVersion(null)}
        >
          <div
            className="max-h-[80vh] w-full max-w-2xl overflow-auto rounded-xl bg-white p-6 shadow-lg"
            onClick={(e) => e.stopPropagation()}
          >
            <h2 className="text-lg font-semibold text-slate-900">
              {previewLoading ? 'Loading...' : `Version ${previewVersion?.versionNumber} — JSON Snapshot`}
            </h2>

            {!previewLoading && (
              <pre className="mt-4 max-h-[55vh] overflow-auto rounded-lg bg-slate-900 p-4 text-xs text-slate-100">
                {JSON.stringify(previewVersion.jsonSchema, null, 2)}
                {console.log(previewVersion.jsonSchema)}
              </pre>
            )}

            <div className="mt-6 flex justify-end">
              <button
                onClick={() => setPreviewVersion(null)}
                className="rounded-lg border border-slate-300 px-4 py-2 text-sm text-slate-700 hover:bg-slate-100"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Restore confirmation */}
      {restoreTarget && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center bg-black/50"
          onClick={() => setRestoreTarget(null)}
        >
          <div
            className="w-full max-w-sm rounded-xl bg-white p-6 shadow-lg"
            onClick={(e) => e.stopPropagation()}
          >
            <h2 className="text-lg font-semibold text-slate-900">Confirm Restore</h2>
            <p className="mt-2 text-sm text-slate-600">
              This will load Version {restoreTarget}'s JSON into the current working state,
              overwriting current unsaved edits. It will not delete or change any existing
              version history. Continue?
            </p>
            <div className="mt-6 flex justify-end gap-3">
              <button
                onClick={() => setRestoreTarget(null)}
                className="rounded-lg border border-slate-300 px-4 py-2 text-sm text-slate-700 hover:bg-slate-100"
              >
                Cancel
              </button>
              <button
                onClick={confirmRestore}
                className="rounded-lg bg-amber-500 px-4 py-2 text-sm text-white hover:bg-amber-600"
              >
                Restore
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
