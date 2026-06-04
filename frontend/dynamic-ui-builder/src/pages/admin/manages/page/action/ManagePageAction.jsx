import React, { useState } from 'react'
import { Link, useParams } from 'react-router'
import ActionTable from './ActionTable';
import { createPageAction, updatePageAction } from '../../../../../api/actionsPageApi';

const AVAILABLE_ACTIONS = [
  { type: "SUBMIT_FORM", label: "Submit Form", icon: "📤" },
  { type: "FETCH_DATA", label: "Fetch Data", icon: "📥" },
  { type: "NAVIGATE", label: "Navigate", icon: "🧭" },
  { type: "SHOW_TOAST", label: "Show Toast", icon: "🔔" },
  { type: "SET_FIELD_VALUE", label: "Set Field Value", icon: "✏️" },
  { type: "RESET_FORM", label: "Reset Form", icon: "🔄" },
  { type: "CHAIN", label: "Chain Actions", icon: "⛓️" }
];

const defaultFormData = (pageCode) => ({
  uiPagecode: pageCode,
  actionName: "",
  actionType: "SUBMIT_FORM",
  path: "",
  url: "",
  method: "GET",
  toastMessage: "",
  toastType: "success",
  fieldName: "",
  fieldValue: "",
  chainActions: ""
});

export default function ManagePageAction() {
  const { pageCode } = useParams();
  const [selectedAction, setSelectedAction] = useState(null);
  const [formData, setFormData] = useState(defaultFormData(pageCode));
  const [editingId, setEditingId] = useState(null);
  const [saving, setSaving] = useState(false);
  const [refreshKey, setRefreshKey] = useState(0);

  const handleSelect = (item) => {
    setSelectedAction(item.type);
    setFormData((prev) => ({ ...prev, actionType: item.type }));
  };

  const handleChange = (e) => {
    setFormData((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const buildConfig = () => {
    switch (formData.actionType) {
      case "NAVIGATE":
        return { navigate: { path: formData.path } };
      case "FETCH_DATA":
      case "SUBMIT_FORM":
        return { api: { url: formData.url, method: formData.method } };
      case "SHOW_TOAST":
        return { toast: { message: formData.toastMessage, type: formData.toastType } };
      case "SET_FIELD_VALUE":
        return { setField: { fieldName: formData.fieldName, value: formData.fieldValue } };
      case "CHAIN":
        return {
          chain: formData.chainActions
            ?.split(",").map((x) => x.trim()).filter(Boolean),
        };
      default:
        return {};
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      const config = buildConfig();
      const payload = {
        uiPagecode: pageCode,
        actionName: formData.actionName,
        actionType: formData.actionType,
        properties: JSON.stringify(config),
      };

      if (editingId) {
        await updatePageAction(editingId, payload);
      } else {
        await createPageAction(pageCode, payload);
      }

      setRefreshKey((k) => k + 1);
      resetForm();
    } catch (error) {
      console.error('Failed to save action', error);
    } finally {
      setSaving(false);
    }
  };

  const resetForm = () => {
    setFormData(defaultFormData(pageCode));
    setEditingId(null);
    setSelectedAction(null);
  };

  const handleEdit = (action) => {
    let props = {};
    try { props = JSON.parse(action.properties || '{}'); } catch { /* empty */ }

    setEditingId(action.id);
    setSelectedAction(action.actionType);

    setFormData({
      uiPagecode: pageCode,
      actionName: action.actionName || '',
      actionType: action.actionType || 'SUBMIT_FORM',
      path: props?.navigate?.path || '',
      url: props?.api?.url || '',
      method: props?.api?.method || 'GET',
      toastMessage: props?.toast?.message || '',
      toastType: props?.toast?.type || 'success',
      fieldName: props?.setField?.fieldName || '',
      fieldValue: props?.setField?.value || '',
      chainActions: Array.isArray(props?.chain) ? props.chain.join(', ') : '',
    });

    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <div className="space-y-6">
      {/* title bar */}
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-xl font-semibold text-slate-900">Page Actions</h1>
          <p className="text-sm text-slate-500">
            Manage Actions available for page <span className="font-semibold">{pageCode}</span>.
          </p>
        </div>
        <Link
          to="/admin_panel/manage_page"
          className="inline-flex w-fit items-center rounded-full bg-slate-900 px-4 py-2 text-sm font-semibold text-white transition hover:bg-slate-700"
        >
          Back to Pages
        </Link>
      </div>

      {/* add actions form  */}
      <div className="grid gap-6 lg:grid-cols-[280px_1fr]">
        <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
          <h2 className="mb-3 text-sm font-semibold uppercase tracking-[0.2em] text-slate-500">Available Actions</h2>
          <div className="space-y-3">
            {AVAILABLE_ACTIONS.map((item) => (
              <div
                key={item.type}
                onClick={() => handleSelect(item)}
                role="button"
                tabIndex={0}
                className={`rounded-2xl border p-4 bg-slate-50 hover:shadow-md transition-colors cursor-pointer ${
                  selectedAction === item.type ? 'border-cyan-400 bg-cyan-50' : 'border-slate-200'
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

        <div className="space-y-6">
          <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
            <h2 className="mb-4 text-sm font-semibold uppercase tracking-[0.2em] text-slate-500">
              {editingId ? 'Edit Action' : 'Add Action'}
            </h2>

            <form onSubmit={handleSubmit} className="space-y-4">
              {/* action name */}
              <div className="grid gap-2">
                <label className="text-sm font-medium text-slate-700">Action Name *</label>
                <input
                  required
                  name="actionName"
                  value={formData.actionName}
                  onChange={handleChange}
                  placeholder="Enter action name"
                  className="rounded-xl border border-slate-200 px-3 py-2 text-sm outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                />
              </div>

              {/* action type */}
              <div className="grid gap-2">
                <label className="text-sm font-medium text-slate-700">Action Type</label>
                <select
                  name="actionType"
                  value={formData.actionType}
                  onChange={handleChange}
                  className="rounded-xl border border-slate-200 px-3 py-2 text-sm outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                >
                  {AVAILABLE_ACTIONS.map((item) => (
                    <option key={item.type} value={item.type}>{item.label}</option>
                  ))}
                </select>
              </div>

              {formData.actionType === "NAVIGATE" && (
                <div className="grid gap-2">
                  <label className="text-sm font-medium text-slate-700">Path</label>
                  <input
                    name="path"
                    value={formData.path}
                    onChange={handleChange}
                    placeholder="/home"
                    className="rounded-xl border border-slate-200 px-3 py-2 text-sm outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                  />
                </div>
              )}

              {(formData.actionType === "FETCH_DATA" || formData.actionType === "SUBMIT_FORM") && (
                <>
                  <div className="grid gap-2">
                    <label className="text-sm font-medium text-slate-700">API URL</label>
                    <input
                      name="url"
                      value={formData.url}
                      onChange={handleChange}
                      placeholder="/api/users"
                      className="rounded-xl border border-slate-200 px-3 py-2 text-sm outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                    />
                  </div>
                  <div className="grid gap-2">
                    <label className="text-sm font-medium text-slate-700">Method</label>
                    <select
                      name="method"
                      value={formData.method}
                      onChange={handleChange}
                      className="rounded-xl border border-slate-200 px-3 py-2 text-sm outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                    >
                      <option>GET</option>
                      <option>POST</option>
                      <option>PUT</option>
                      <option>DELETE</option>
                    </select>
                  </div>
                </>
              )}

              {formData.actionType === "SHOW_TOAST" && (
                <>
                  <div className="grid gap-2">
                    <label className="text-sm font-medium text-slate-700">Message</label>
                    <input
                      name="toastMessage"
                      value={formData.toastMessage}
                      onChange={handleChange}
                      className="rounded-xl border border-slate-200 px-3 py-2 text-sm outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                    />
                  </div>
                  <div className="grid gap-2">
                    <label className="text-sm font-medium text-slate-700">Type</label>
                    <select
                      name="toastType"
                      value={formData.toastType}
                      onChange={handleChange}
                      className="rounded-xl border border-slate-200 px-3 py-2 text-sm outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                    >
                      <option value="success">Success</option>
                      <option value="error">Error</option>
                      <option value="warning">Warning</option>
                      <option value="info">Info</option>
                    </select>
                  </div>
                </>
              )}

              {formData.actionType === "SET_FIELD_VALUE" && (
                <>
                  <div className="grid gap-2">
                    <label className="text-sm font-medium text-slate-700">Field Name</label>
                    <input
                      name="fieldName"
                      value={formData.fieldName}
                      onChange={handleChange}
                      className="rounded-xl border border-slate-200 px-3 py-2 text-sm outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                    />
                  </div>
                  <div className="grid gap-2">
                    <label className="text-sm font-medium text-slate-700">Field Value</label>
                    <input
                      name="fieldValue"
                      value={formData.fieldValue}
                      onChange={handleChange}
                      className="rounded-xl border border-slate-200 px-3 py-2 text-sm outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                    />
                  </div>
                </>
              )}

              {formData.actionType === "CHAIN" && (
                <div className="grid gap-2">
                  <label className="text-sm font-medium text-slate-700">Action Names (comma-separated)</label>
                  <input
                    name="chainActions"
                    value={formData.chainActions}
                    onChange={handleChange}
                    placeholder="saveUser, showSuccess, navigateHome"
                    className="rounded-xl border border-slate-200 px-3 py-2 text-sm outline-none focus:border-cyan-400 focus:ring-2 focus:ring-cyan-100"
                  />
                </div>
              )}

              <div className="flex gap-2">
                <button
                  type="submit"
                  disabled={saving}
                  className="inline-flex items-center justify-center rounded-full bg-cyan-500 px-5 py-2 text-sm font-semibold text-white hover:bg-cyan-400 disabled:opacity-60"
                >
                  {saving ? 'Saving...' : editingId ? 'Update Action' : 'Add Action'}
                </button>

                {editingId && (
                  <button
                    type="button"
                    onClick={resetForm}
                    className="inline-flex items-center justify-center rounded-full bg-slate-200 px-5 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-300"
                  >
                    Cancel
                  </button>
                )}
              </div>
            </form>
          </div>

          <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
            <div className="mb-4 flex items-center justify-between">
              <div>
                <h2 className="text-sm font-semibold uppercase tracking-[0.2em] text-slate-500">Actions on this page</h2>
                <p className="text-sm text-slate-500">Review the page-specific Action list below.</p>
              </div>
            </div>
            <div className="overflow-hidden rounded-2xl border border-slate-200">
              <ActionTable pageCode={pageCode} onEdit={handleEdit} refreshKey={refreshKey} />
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
