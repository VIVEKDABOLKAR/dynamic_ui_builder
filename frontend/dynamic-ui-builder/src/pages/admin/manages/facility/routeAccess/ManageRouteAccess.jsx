import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import toast from 'react-hot-toast'
import { Checkbox, MenuItem, Select } from '@mui/material'
import {
  getFacilityRouteAccess,
  getGlobalRouteAccess,
  updateFacilityRouteAccess,
  updateGlobalRouteAccess,
} from '../../../../../api/routeAccessApi'
import { useFacility } from '../../../../../context/FacilityV2Context'

const GLOBAL_SCOPE = '' // sentinel value for the "Global" dropdown option

export default function ManageRouteAccess() {
  // Facility picker is driven entirely by the shared context now. Note:
  // `facilities` here is only the facilities THIS admin has accessible
  // approval for, and picking one calls `changeFacility`, which switches
  // the admin's own active session facility (JWT + localStorage) as a
  // side effect. Intentional per your call — just documenting it here.
  const { facilities, selectedFacility, loading: facilityLoading, changeFacility } = useFacility()

  const [isGlobalScope, setIsGlobalScope] = useState(true)
  const [routes, setRoutes] = useState([]) // [{routeId, routeCode, pageName, path, granted}]
  const [initialGranted, setInitialGranted] = useState(new Set())
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)

  const loadAccess = async (facilityId) => {
    setLoading(true)
    try {
      const data = facilityId
        ? await getFacilityRouteAccess(facilityId)
        : await getGlobalRouteAccess()

        console.log(data)

      const list = data.routes || []
      setRoutes(list)
      setInitialGranted(new Set(list.filter((r) => r.granted).map((r) => r.routeId)))
    } catch {
      toast.error('Failed to load route access')
    } finally {
      setLoading(false)
    }
  }

  // Default view is Global — we never call changeFacility on mount, only
  // when the admin explicitly picks a facility from the dropdown.
  useEffect(() => {
    loadAccess(selectedFacility?.id ?? GLOBAL_SCOPE)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedFacility?.id])

  const dropdownValue = isGlobalScope ? GLOBAL_SCOPE : (selectedFacility?.id ?? GLOBAL_SCOPE)

  const handleScopeChange = async (value) => {
    if (value === GLOBAL_SCOPE) {
      setIsGlobalScope(true)
      loadAccess(GLOBAL_SCOPE)
      return
    }

    setIsGlobalScope(false)
    await changeFacility(value) // switches the admin's active session facility
    loadAccess(value)
  }

  const handleToggle = (routeId) => {
    setRoutes((prev) =>
      prev.map((r) => (r.routeId === routeId ? { ...r, granted: !r.granted } : r))
    )
  }

  const grantedIds = routes.filter((r) => r.granted).map((r) => r.routeId)
  const grantedSet = new Set(grantedIds)
  const isDirty =
    grantedSet.size !== initialGranted.size ||
    [...grantedSet].some((id) => !initialGranted.has(id))

  const handleSave = async () => {
    setSaving(true)
    try {
      if (isGlobalScope) {
        if (
          !window.confirm(
            'This applies the selected pages to every facility, overwriting each facility\'s current access. Continue?'
          )
        ) {
          setSaving(false)
          return
        }
        await updateGlobalRouteAccess(grantedIds)
        toast.success('Applied to all facilities')
      } else {
        await updateFacilityRouteAccess(selectedFacility.id, grantedIds)
        toast.success('Route access updated')
      }
      loadAccess(isGlobalScope ? GLOBAL_SCOPE : selectedFacility.id)
    } catch (err) {
      toast.error(err?.response?.data?.message || err?.response?.data || 'Failed to save route access')
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
          <span className="text-slate-500">Route Access Management</span>
        </div>
        <h1 className="mt-2 text-2xl font-bold text-slate-900">Route Access Management</h1>
        <p className="mt-1 text-sm text-slate-500">
          Control which application pages each facility can see and access — no code changes required.
        </p>
{/* 
        <div className="mt-4 flex items-center gap-2">
          <span className="text-sm font-medium text-slate-600">Facility:</span>
          <Select
            size="small"
            value={dropdownValue}
            onChange={(e) => handleScopeChange(e.target.value)}
            disabled={facilityLoading}
            sx={{ minWidth: 260 }}
          >
            <MenuItem value={GLOBAL_SCOPE}>Global</MenuItem>
            {facilities.map((f) => (
              <MenuItem key={f.id} value={f.id}>
                {f.name} ({f.id})
              </MenuItem>
            ))}
          </Select>
        </div> */}
      </div>


      <div className="rounded-2xl border border-slate-200 bg-white shadow-sm">
        {loading ? (
          <div className="p-6 text-sm text-slate-500">Loading route access…</div>
        ) : routes.length === 0 ? (
          <div className="p-6 text-sm text-slate-500">No active routes found.</div>
        ) : (
          <table className="w-full text-sm">
            <thead className="bg-slate-50 text-left text-xs font-semibold uppercase text-slate-500">
              <tr>
                <th className="px-5 py-3 w-12"></th>
                <th className="px-5 py-3">Page</th>
                <th className="px-5 py-3">Path</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {routes.map((route) => (
                <tr key={route.routeId}>
                  <td className="px-5 py-2">
                    <Checkbox
                      size="small"
                      checked={route.granted}
                      onChange={() => handleToggle(route.routeId)}
                    />
                  </td>
                  <td className="px-5 py-2 font-medium text-slate-900">
                    {route.pageName || route.routeCode}
                  </td>
                  <td className="px-5 py-2 font-mono text-xs text-slate-500">{route.path}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}

        <div className="flex justify-end border-t border-slate-100 px-5 py-3">
          <button
            onClick={handleSave}
            disabled={loading || saving || !isDirty}
            className="rounded-full bg-cyan-400 px-5 py-2 text-sm font-semibold hover:bg-cyan-300 disabled:opacity-50"
          >
            {saving ? 'Saving...' : 'Save'}
          </button>
        </div>
      </div>
    </div>
  )
}