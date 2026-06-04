import React, { useState } from 'react'
import { createComponentAction, createPageAction } from '../../../../../api/actionsPageApi';

/**
 * Small inline modal – lets users link a named action + ref to a component.
 * Props:
 *   component  – the component row { id, componentName, pageCode, ... }
 *   onClose    – called when the modal is dismissed (saved or cancelled)
 */
export default function ComponentActionModal({ component, onClose }) {
  const [event, setEvent] = useState('');
  const [actionRef, setActionRef] = useState('');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  console.log(component)

  const handleSave = async (e) => {
    e.preventDefault();
    setError('');

    if (!event.trim()) {
      setError('Action name is required.');
      return;
    }
    if (!actionRef.trim()) {
      setError('Action ref is required.');
      return;
    }

    setSaving(true);
    try {
      const payload = {
        uiPagecode: component.pageCode,
        componentId: component.id,
        event: event.trim(),
        ref: actionRef.trim(),
        condition: "true"
      };
      await createComponentAction(component.pageCode, payload);
      onClose(true);
    } catch (err) {
      console.error('Failed to save action', err);
      setError('Failed to save action. Please try again.');
    } finally {
      setSaving(false);
    }
  };

  return (
    /* backdrop */
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4"
      onClick={(e) => { if (e.target === e.currentTarget) onClose(false); }}
    >
      <div className="w-full max-w-md rounded-2xl border border-slate-200 bg-white p-6 shadow-xl">
        <div className="mb-4 flex items-start justify-between">
          <div>
            <h2 className="text-base font-semibold text-slate-900">Add Action to Component</h2>
            <p className="mt-0.5 text-sm text-slate-500">
              Component: <span className="font-semibold text-slate-700">{component.componentName}</span>
            </p>
          </div>
          <button
            type="button"
            onClick={() => onClose(false)}
            className="ml-4 flex h-7 w-7 items-center justify-center rounded-full text-slate-400 hover:bg-slate-100 hover:text-slate-600"
            aria-label="Close"
          >
            ✕
          </button>
        </div>

        {error && (
          <div className="mb-4 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            {error}
          </div>
        )}

        <form onSubmit={handleSave} className="space-y-4">
          <div className="grid gap-1.5">
            <label className="text-sm font-medium text-slate-700">
              Event Name <span className="text-red-500">*</span>
            </label>
            <input
              autoFocus
              value={event}
              onChange={(e) => setEvent(e.target.value)}
              placeholder="e.g. onClick"
              className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none transition focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
            />
            <p className="text-xs text-slate-400">event type</p>
          </div>

          <div className="grid gap-1.5">
            <label className="text-sm font-medium text-slate-700">
              Action Ref <span className="text-red-500">*</span>
            </label>
            <input
              value={actionRef}
              onChange={(e) => setActionRef(e.target.value)}
              placeholder="e.g. saveHome"
              className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-slate-900 outline-none transition focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
            />
            <p className="text-xs text-slate-400">The  reference this action is bound to.</p>
          </div>

          <div className="flex justify-end gap-2 pt-2">
            <button
              type="button"
              onClick={() => onClose(false)}
              className="inline-flex items-center rounded-full bg-slate-100 px-4 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-200"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={saving}
              className="inline-flex items-center rounded-full bg-cyan-500 px-4 py-2 text-sm font-semibold text-white hover:bg-cyan-400 disabled:opacity-60"
            >
              {saving ? 'Saving...' : 'Save Action'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
