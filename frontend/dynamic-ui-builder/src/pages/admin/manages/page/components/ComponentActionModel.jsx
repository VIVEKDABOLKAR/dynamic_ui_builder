import React, { useEffect, useState } from 'react'
import {
  getComponentActions,
  createComponentAction,
  updateComponentAction,
  deleteComponentAction,
} from '../../../../../api/actionsPageApi'

const EVENT_OPTIONS = ['onClick', 'onChange', 'onLoad', 'onBlur', 'onFocus', 'onSubmit', 'others']

const EMPTY_FORM = {
  event: 'onClick',
  customEvent: '',
  actionRef: '',
  conditionExpr: 'true',
  sequenceNo: 0,
}

export default function ComponentActionModal({ component, pageActions = [], onClose }) {
  const [actions, setActions] = useState([])
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const [form, setForm] = useState(EMPTY_FORM)
  const [error, setError] = useState('')

  const loadActions = async () => {
    setLoading(true)
    try {
      const data = await getComponentActions(component.id)
      setActions(Array.isArray(data) ? data : [])
    } catch (err) {
      console.error('Failed to load component actions', err)
      setActions([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadActions()
  }, [component.id])

  const resetForm = () => {
    setForm(EMPTY_FORM)
    setEditingId(null)
    setError('')
  }

  const handleEdit = (action) => {
    setEditingId(action.id)
    // If the stored event isn't in the predefined list, it was a custom one
    const isCustom = !EVENT_OPTIONS.filter(e => e !== 'others').includes(action.event)
    setForm({
      event: isCustom ? 'others' : (action.event || 'onClick'),
      customEvent: isCustom ? (action.event || '') : '',
      actionRef: action.actionRef || '',
      conditionExpr: action.conditionExpr || 'true',
      sequenceNo: action.sequenceNo ?? 0,
    })
    setError('')
  }

  const handleChange = (e) => {
    const { name, value } = e.target
    setForm((prev) => {
      // When switching away from "others", clear the customEvent field
      if (name === 'event' && value !== 'others') {
        return { ...prev, event: value, customEvent: '' }
      }
      return { ...prev, [name]: name === 'sequenceNo' ? Number(value) : value }
    })
  }

  // The actual event value sent to the API: use customEvent when "others" is selected
  const resolvedEvent = form.event === 'others' ? form.customEvent : form.event

  const handleSave = async (e) => {
    e.preventDefault()
    setError('')

    if (!resolvedEvent.trim()) {
      setError(form.event === 'others' ? 'Please enter a custom event name.' : 'Event is required.')
      return
    }
    if (!form.actionRef.trim()) { setError('Action Ref is required.'); return }

    setSaving(true)
    try {
      const payload = {
        componentId: component.id,
        pageCode: component.pageCode,
        event: resolvedEvent.trim(),
        actionRef: form.actionRef.trim(),
        conditionExpr: form.conditionExpr.trim() || 'true',
        sequenceNo: form.sequenceNo,
      }

      if (editingId) {
        await updateComponentAction(editingId, payload)
      } else {
        await createComponentAction(payload)
      }

      await loadActions()
      resetForm()
    } catch (err) {
      console.error('Failed to save component action', err)
      setError('Failed to save. Please try again.')
    } finally {
      setSaving(false)
    }
  }

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this action binding?')) return
    try {
      await deleteComponentAction(id)
      await loadActions()
      if (editingId === id) resetForm()
    } catch (err) {
      console.error('Failed to delete component action', err)
    }
  }

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4"
      onClick={(e) => { if (e.target === e.currentTarget) onClose() }}
    >
      <div className="flex w-full max-w-2xl flex-col gap-5 rounded-2xl border border-slate-200 bg-white p-6 shadow-xl max-h-[95vh] overflow-y-auto">

        {/* header */}
        <div className="flex items-start justify-between">
          <div>
            <h2 className="text-base font-semibold text-slate-900">Component Actions</h2>
            <p className="mt-0.5 text-sm text-slate-500">
              Component: <span className="font-semibold text-slate-700">{component.componentName}</span>
              <span className="ml-2 text-xs text-slate-400">(id: {component.id})</span>
            </p>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="ml-4 flex h-7 w-7 items-center justify-center rounded-full text-slate-400 hover:bg-slate-100 hover:text-slate-600"
          >
            ✕
          </button>
        </div>

        {/* ── add / edit form ── */}
        <div className="rounded-xl border border-slate-200 bg-slate-50 p-4">
          <h3 className="mb-3 text-sm font-semibold text-slate-700">
            {editingId ? 'Edit Action Binding' : 'Add Action Binding'}
          </h3>

          {error && (
            <div className="mb-3 rounded-xl border border-red-200 bg-red-50 px-4 py-2 text-sm text-red-700">
              {error}
            </div>
          )}

          <form onSubmit={handleSave} className="grid gap-3 sm:grid-cols-2">

            {/* event */}
            <div className="grid gap-1.5">
              <label className="text-xs font-medium text-slate-600">
                Event <span className="text-red-500">*</span>
              </label>
              <select
                name="event"
                value={form.event}
                onChange={handleChange}
                className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
              >
                {EVENT_OPTIONS.map((ev) => (
                  <option key={ev} value={ev}>{ev}</option>
                ))}
              </select>

              {/* custom event input — only visible when "others" is selected */}
              {form.event === 'others' && (
                <input
                  autoFocus
                  name="customEvent"
                  value={form.customEvent}
                  onChange={handleChange}
                  placeholder="Type custom event name…"
                  className="rounded-xl border border-cyan-300 bg-white px-3 py-2 text-sm text-slate-900 outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                />
              )}
            </div>

            {/* action ref */}
            <div className="grid gap-1.5">
              <label className="text-xs font-medium text-slate-600">
                Action Ref <span className="text-red-500">*</span>
              </label>
              {pageActions.length > 0 ? (
                <select
                  name="actionRef"
                  value={form.actionRef}
                  onChange={handleChange}
                  className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                >
                  <option value="">Select page action...</option>
                  {pageActions.map((a) => (
                    <option key={a.id} value={a.actionName}>
                      {a.actionName} ({a.actionType})
                    </option>
                  ))}
                </select>
              ) : (
                <input
                  name="actionRef"
                  value={form.actionRef}
                  onChange={handleChange}
                  placeholder="e.g. saveHome"
                  className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                />
              )}
              <p className="text-xs text-slate-400">References a key in this page's actions.</p>
            </div>

            {/* condition */}
            <div className="grid gap-1.5">
              <label className="text-xs font-medium text-slate-600">Condition</label>
              <input
                name="conditionExpr"
                value={form.conditionExpr}
                onChange={handleChange}
                placeholder="true"
                className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
              />
              <p className="text-xs text-slate-400">JS-like expression. Leave "true" to always fire.</p>
            </div>

            {/* sequence */}
            <div className="grid gap-1.5">
              <label className="text-xs font-medium text-slate-600">Sequence</label>
              <input
                type="number"
                name="sequenceNo"
                value={form.sequenceNo}
                onChange={handleChange}
                min={0}
                className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
              />
              <p className="text-xs text-slate-400">Order when multiple actions fire on the same event.</p>
            </div>

            {/* buttons */}
            <div className="col-span-full flex gap-2 pt-1">
              <button
                type="submit"
                disabled={saving}
                className="inline-flex items-center rounded-full bg-cyan-500 px-4 py-2 text-sm font-semibold text-white hover:bg-cyan-400 disabled:opacity-60"
              >
                {saving ? 'Saving…' : editingId ? 'Update' : 'Add Action'}
              </button>
              {editingId && (
                <button
                  type="button"
                  onClick={resetForm}
                  className="inline-flex items-center rounded-full bg-slate-200 px-4 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-300"
                >
                  Cancel
                </button>
              )}
            </div>
          </form>
        </div>

        {/* ── existing action bindings table ── */}
        <div>
          <h3 className="mb-2 text-sm font-semibold text-slate-700">Existing Bindings</h3>

          {loading ? (
            <div className="flex items-center gap-3 py-6 text-sm text-slate-500">
              <span className="h-4 w-4 animate-spin rounded-full border-2 border-slate-300 border-t-cyan-500" />
              Loading…
            </div>
          ) : actions.length === 0 ? (
            <div className="rounded-xl border border-dashed border-slate-200 py-8 text-center text-sm text-slate-400">
              No action bindings yet. Add one above.
            </div>
          ) : (
            <div className="overflow-hidden rounded-xl border border-slate-200">
              <table className="w-full text-sm">
                <thead className="bg-slate-50 text-xs font-semibold uppercase tracking-wider text-slate-500">
                  <tr>
                    <th className="px-4 py-3 text-left">Event</th>
                    <th className="px-4 py-3 text-left">Action Ref</th>
                    <th className="px-4 py-3 text-left">Condition</th>
                    <th className="px-4 py-3 text-center">Seq</th>
                    <th className="px-4 py-3 text-right">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {actions.map((action) => (
                    <tr key={action.id} className={`transition hover:bg-slate-50 ${editingId === action.id ? 'bg-cyan-50' : ''}`}>
                      <td className="px-4 py-3">
                        <span className="rounded-full bg-purple-100 px-2 py-0.5 text-xs font-semibold text-purple-700">
                          {action.event}
                        </span>
                      </td>
                      <td className="px-4 py-3">
                        <span className="rounded-full bg-cyan-100 px-2 py-0.5 text-xs font-semibold text-cyan-700">
                          {action.actionRef}
                        </span>
                      </td>
                      <td className="px-4 py-3 text-slate-500 text-xs font-mono">
                        {action.conditionExpr || 'true'}
                      </td>
                      <td className="px-4 py-3 text-center text-slate-500">
                        {action.sequenceNo ?? 0}
                      </td>
                      <td className="px-4 py-3">
                        <div className="flex justify-end gap-2">
                          <button
                            type="button"
                            onClick={() => handleEdit(action)}
                            className="rounded-md bg-amber-100 px-2 py-1 text-xs font-semibold text-amber-700 hover:bg-amber-200"
                          >
                            Edit
                          </button>
                          <button
                            type="button"
                            onClick={() => handleDelete(action.id)}
                            className="rounded-md bg-red-100 px-2 py-1 text-xs font-semibold text-red-700 hover:bg-red-200"
                          >
                            Delete
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {/* close button */}
        <div className="flex justify-end border-t border-slate-100 pt-3">
          <button
            type="button"
            onClick={onClose}
            className="inline-flex items-center rounded-full bg-slate-900 px-5 py-2 text-sm font-semibold text-white hover:bg-slate-700"
          >
            Done
          </button>
        </div>
      </div>
    </div>
  )
}