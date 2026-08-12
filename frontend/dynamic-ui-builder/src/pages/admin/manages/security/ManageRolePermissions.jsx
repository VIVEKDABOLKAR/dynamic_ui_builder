import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import toast from 'react-hot-toast'
import { getRoles, updateRolePermissions } from '../../../../api/rolePermissionApi'
import AddRoleDialog from './AddRoleDialog'

/**
 * Security Configuration — lets an admin edit each role's permission
 * patterns (e.g. "*", "gate.*", "*.view") without a redeploy.
 *
 * These patterns only gate the JSON-driven dynamic pages (page open /
 * route resolve) — they do not affect the hardcoded business APIs or
 * facility access, which are managed elsewhere (Route Access, Facility
 * Approvals).
 *
 * A role with NO patterns is fail-open (every page allowed) until you
 * add at least one pattern here — mirrors how Route Access behaves for
 * an unconfigured facility.
 */
export default function ManageRolePermissions() {
  const [roles, setRoles] = useState([]) // [{ code, name, patterns: [...] }]
  const [selectedCode, setSelectedCode] = useState(null)
  const [patterns, setPatterns] = useState([])
  const [initialPatterns, setInitialPatterns] = useState([])
  const [newPattern, setNewPattern] = useState('')
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [addRoleOpen, setAddRoleOpen] = useState(false)

  const loadRoles = async (keepSelection) => {
    setLoading(true)
    try {
      const data = await getRoles()
      setRoles(data)

      const nextCode = keepSelection && data.some((r) => r.code === keepSelection)
        ? keepSelection
        : data[0]?.code ?? null

      selectRole(nextCode, data)
    } catch {
      toast.error('Failed to load roles')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadRoles()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const selectRole = (code, roleList = roles) => {
    setSelectedCode(code)
    const role = roleList.find((r) => r.code === code)
    const rolePatterns = role?.patterns ?? []
    setPatterns(rolePatterns)
    setInitialPatterns(rolePatterns)
    setNewPattern('')
  }

  const selectedRole = roles.find((r) => r.code === selectedCode)

  const isDirty =
    patterns.length !== initialPatterns.length ||
    patterns.some((p, i) => p !== initialPatterns[i])

  const handleAddPattern = () => {
    const value = newPattern.trim()
    if (!value) return
    if (patterns.includes(value)) {
      toast.error('That pattern is already added')
      return
    }
    setPatterns((prev) => [...prev, value])
    setNewPattern('')
  }

  const handleRemovePattern = (pattern) => {
    setPatterns((prev) => prev.filter((p) => p !== pattern))
  }

  const handleSave = async () => {
    if (!selectedCode) return
    setSaving(true)
    try {
      await updateRolePermissions(selectedCode, patterns)
      toast.success('Permissions updated')
      await loadRoles(selectedCode)
    } catch (err) {
      toast.error(err?.response?.data?.message || err?.response?.data || 'Failed to save permissions')
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="flex flex-col gap-6">
      <div>
        <div className="flex items-center gap-1 text-xs text-slate-400">
          <Link to="/admin_panel/overview" className="hover:text-slate-600">Admin</Link>
          <span>›</span>
          <span className="text-slate-500">Security Configuration</span>
        </div>
        <div className="mt-2 flex items-center justify-between">
          <h1 className="text-2xl font-bold text-slate-900">Security Configuration</h1>
          <button
            onClick={() => setAddRoleOpen(true)}
            className="rounded-full bg-slate-900 px-4 py-2 text-sm font-semibold text-white hover:bg-slate-800"
          >
            + Add Role
          </button>
        </div>
        <p className="mt-1 text-sm text-slate-500">
          Control which dynamic pages each role can open using permission patterns — no code changes required.
        </p>
      </div>
      <AddRoleDialog
        open={addRoleOpen}
        onClose={() => setAddRoleOpen(false)}
        onCreated={(role) => loadRoles(role.code)}
      />

      <div className="grid grid-cols-1 gap-6 md:grid-cols-[240px_1fr]">
        {/* Role list */}
        <div className="rounded-2xl border border-slate-200 bg-white shadow-sm">
          {loading && roles.length === 0 ? (
            <div className="p-4 text-sm text-slate-500">Loading roles…</div>
          ) : (
            <ul className="divide-y divide-slate-100">
              {roles.map((role) => (
                <li key={role.code}>
                  <button
                    onClick={() => selectRole(role.code)}
                    
                    className={`w-full px-4 py-3 text-left text-sm cursor-pointer transition-colors ${role.code === selectedCode
                      ? 'bg-slate-900 text-white'
                      : 'text-slate-700 hover:bg-slate-50'
                      }`}
                  >
                    <div className="font-semibold">{role.name}</div>
                    <div className={`text-xs ${role.code === selectedCode ? 'text-slate-300' : 'text-slate-400'}`}>
                      {role.code} · {role.patterns.length} pattern{role.patterns.length === 1 ? '' : 's'}
                    </div>
                  </button>
                </li>
              ))}
            </ul>
          )}
        </div>

        {/* Pattern editor */}
        <div className="rounded-2xl border border-slate-200 bg-white shadow-sm">
          {!selectedRole ? (
            <div className="p-6 text-sm text-slate-500">Select a role to manage its permissions.</div>
          ) : (
            <>
              <div className="border-b border-slate-100 px-5 py-4">
                <div className="text-sm font-semibold text-slate-900">{selectedRole.name}</div>
                <div className="text-xs font-mono text-slate-400">{selectedRole.code}</div>
                {patterns.length === 0 && (
                  <div className="mt-2 rounded-lg bg-amber-50 px-3 py-2 text-xs text-amber-700">
                    No patterns configured — this role currently has access to <b>every</b> dynamic page (fail-open).
                    Add a pattern below to start restricting it.
                  </div>
                )}
              </div>

              <div className="px-5 py-4">
                <div className="flex flex-wrap gap-2">
                  {patterns.map((pattern) => (
                    <span
                      key={pattern}
                      className="inline-flex items-center gap-2 rounded-full bg-slate-100 px-3 py-1 text-xs font-mono text-slate-700"
                    >
                      {pattern}
                      <button
                        onClick={() => handleRemovePattern(pattern)}
                        className="text-slate-400 hover:text-red-500"
                        aria-label={`Remove ${pattern}`}
                      >
                        ×
                      </button>
                    </span>
                  ))}
                  {patterns.length === 0 && (
                    <span className="text-xs text-slate-400">No patterns added yet.</span>
                  )}
                </div>

                <div className="mt-4 flex items-center gap-2">
                  <input
                    value={newPattern}
                    onChange={(e) => setNewPattern(e.target.value)}
                    onKeyDown={(e) => {
                      if (e.key === 'Enter') {
                        e.preventDefault()
                        handleAddPattern()
                      }
                    }}
                    placeholder="e.g. gate.*, parking.checkin, *.view"
                    className="flex-1 rounded-lg border border-slate-200 px-3 py-2 text-sm font-mono focus:border-slate-400 focus:outline-none"
                  />
                  <button
                    onClick={handleAddPattern}
                    disabled={!newPattern.trim()}
                    className="rounded-lg border border-slate-200 px-4 py-2 text-sm font-medium text-slate-700 hover:bg-slate-50 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    Add
                  </button>
                </div>

                <p className="mt-3 text-xs text-slate-400">
                  Patterns: <code className="font-mono">*</code> (everything),{' '}
                  <code className="font-mono">gate.*</code> (all gate pages),{' '}
                  <code className="font-mono">*.view</code> (any view-only page), or an exact permission like{' '}
                  <code className="font-mono">gate.checkin</code>.
                </p>
              </div>

              <div className="flex justify-end border-t border-slate-100 px-5 py-3">
                <button
                  onClick={handleSave}
                  disabled={saving || !isDirty}
                  className="rounded-full bg-cyan-400 px-5 py-2 text-sm font-semibold hover:bg-cyan-300 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {saving ? 'Saving...' : 'Save'}
                </button>
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  )
}
