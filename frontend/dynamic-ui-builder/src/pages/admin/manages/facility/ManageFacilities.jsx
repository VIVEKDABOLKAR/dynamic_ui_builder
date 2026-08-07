import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import toast from 'react-hot-toast'
import { FaEdit, FaTrash } from 'react-icons/fa'
import {
  getFacilities,
  createFacility,
  updateFacility,
  deleteFacility,
} from '../../../../api/facilityApi'
import FacilityDialog from '../../../../components/admin/facility/FacilityDialog'

export default function ManageFacilities() {
  const [facilities, setFacilities] = useState([])
  const [loading, setLoading] = useState(true)
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editingFacility, setEditingFacility] = useState(null)
  const [deletingId, setDeletingId] = useState(null)

  const loadFacilities = () => {
    setLoading(true)
    getFacilities()
      .then((data) => setFacilities(data || []))
      .catch(() => toast.error('Failed to load facilities'))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    loadFacilities()
  }, [])

  const handleAdd = () => {
    setEditingFacility(null)
    setDialogOpen(true)
  }

  const handleEdit = (facility) => {
    setEditingFacility(facility)
    setDialogOpen(true)
  }

  const handleSave = async (formData) => {
    try {
      if (editingFacility) {
        await updateFacility(editingFacility.id, { name: formData.name })
        toast.success('Facility updated')
      } else {
        await createFacility({ id: formData.id, name: formData.name })
        toast.success('Facility created')
      }
      setDialogOpen(false)
      loadFacilities()
    } catch (err) {
      toast.error(err?.response?.data?.message || err?.response?.data || 'Failed to save facility')
    }
  }

  const handleDelete = async (facility) => {
    if (!window.confirm(`Delete facility "${facility.name}" (${facility.id})? This cannot be undone.`)) {
      return
    }

    setDeletingId(facility.id)
    try {
      await deleteFacility(facility.id)
      toast.success('Facility deleted')
      setFacilities((prev) => prev.filter((f) => f.id !== facility.id))
    } catch (err) {
      toast.error(err?.response?.data?.message || err?.response?.data || 'Failed to delete facility')
    } finally {
      setDeletingId(null)
    }
  }

  return (
    <div className="flex flex-col gap-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <div className="flex items-center gap-1 text-xs text-slate-400">
            <Link to="/admin_panel/overview" className="hover:text-slate-600">Admin</Link>
            <span>›</span>
            <span className="text-slate-500">Manage Facilities</span>
          </div>
          <h1 className="mt-2 text-2xl font-bold text-slate-900">Manage Facilities</h1>
          <p className="mt-1 text-sm text-slate-500">
            Add, rename, or remove facilities available in the yard management system.
          </p>
        </div>

        <button
          onClick={handleAdd}
          className="rounded-full bg-cyan-400 px-4 py-2 text-sm font-semibold hover:bg-cyan-300"
        >
          + Add Facility
        </button>
      </div>

      {loading ? (
        <div className="p-6 text-sm text-slate-500">Loading facilities…</div>
      ) : facilities.length === 0 ? (
        <div className="rounded-xl border border-slate-200 bg-white p-6 text-sm text-slate-500 shadow-sm">
          No facilities yet. Click "Add Facility" to create one.
        </div>
      ) : (
        <div className="overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
          <table className="w-full text-sm">
            <thead className="bg-slate-50 text-left text-xs font-semibold uppercase text-slate-500">
              <tr>
                <th className="px-5 py-3">Facility ID</th>
                <th className="px-5 py-3">Facility Name</th>
                <th className="px-5 py-3 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {facilities.map((facility) => (
                <tr key={facility.id}>
                  <td className="px-5 py-3 font-medium text-slate-900">{facility.id}</td>
                  <td className="px-5 py-3 text-slate-600">{facility.name}</td>
                  <td className="px-5 py-3 text-right">
                    <div className="flex justify-end gap-2">
                      <button
                        type="button"
                        onClick={() => handleEdit(facility)}
                        className="rounded-full bg-slate-900 p-2 text-white hover:bg-slate-700"
                        title="Edit"
                      >
                        <FaEdit size={14} />
                      </button>
                      <button
                        type="button"
                        onClick={() => handleDelete(facility)}
                        disabled={deletingId === facility.id}
                        className="rounded-full bg-red-500 p-2 text-white hover:bg-red-400 disabled:opacity-60"
                        title="Delete"
                      >
                        <FaTrash size={14} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <FacilityDialog
        open={dialogOpen}
        onClose={() => setDialogOpen(false)}
        onSave={handleSave}
        editingFacility={editingFacility}
      />
    </div>
  )
}
