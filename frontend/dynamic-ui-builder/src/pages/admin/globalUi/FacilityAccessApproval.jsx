import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import toast from 'react-hot-toast'
import {
  getPendingFacilityRequests,
  approveFacilityRequest,
  rejectFacilityRequest,
} from '../../../api/facilityApi'   ;

export default function FacilityAccessApproval() {
  const [requests, setRequests] = useState([])
  const [loading, setLoading] = useState(true)
  const [actioningId, setActioningId] = useState(null)

  const loadRequests = () => {
    setLoading(true)
    getPendingFacilityRequests()
      .then((data) => setRequests(data || []))
      .catch(() => toast.error('Failed to load pending requests'))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    loadRequests()
  }, [])

  const handleApprove = async (id) => {
    setActioningId(id)
    try {
      await approveFacilityRequest(id)
      toast.success('Access approved')
      setRequests((prev) => prev.filter((r) => r.id !== id))
    } catch {
      toast.error('Failed to approve')
    } finally {
      setActioningId(null)
    }
  }

  const handleReject = async (id) => {
    setActioningId(id)
    try {
      await rejectFacilityRequest(id)
      toast.success('Access rejected')
      setRequests((prev) => prev.filter((r) => r.id !== id))
    } catch {
      toast.error('Failed to reject')
    } finally {
      setActioningId(null)
    }
  }

  return (
    <div className="flex flex-col gap-6">
      <div>
        <div className="flex items-center gap-1 text-xs text-slate-400">
          <Link to="/admin_panel/overview" className="hover:text-slate-600">Admin</Link>
          <span>›</span>
          <span className="text-slate-500">Facility Access Requests</span>
        </div>
        <h1 className="mt-2 text-2xl font-bold text-slate-900">Facility Access Requests</h1>
        <p className="mt-1 text-sm text-slate-500">
          Review and approve or reject pending facility access requests from new users.
        </p>
      </div>

      {loading ? (
        <div className="p-6 text-sm text-slate-500">Loading requests…</div>
      ) : requests.length === 0 ? (
        <div className="rounded-xl border border-slate-200 bg-white p-6 text-sm text-slate-500 shadow-sm">
          No pending requests.
        </div>
      ) : (
        <div className="overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
          <table className="w-full text-sm">
            <thead className="bg-slate-50 text-left text-xs font-semibold uppercase text-slate-500">
              <tr>
                <th className="px-5 py-3">Username</th>
                <th className="px-5 py-3">Facility ID</th>
                <th className="px-5 py-3 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {requests.map((req) => (
                <tr key={req.id}>
                  <td className="px-5 py-3 font-medium text-slate-900">{req.user?.username}</td>
                  <td className="px-5 py-3 text-slate-600">{req.facilityId}</td>
                  <td className="px-5 py-3 text-right">
                    <div className="flex justify-end gap-2">
                      <button
                        type="button"
                        onClick={() => handleApprove(req.id)}
                        disabled={actioningId === req.id}
                        className="rounded-lg bg-green-600 px-3 py-1.5 text-xs font-medium text-white hover:bg-green-700 disabled:opacity-60"
                      >
                        Approve
                      </button>
                      <button
                        type="button"
                        onClick={() => handleReject(req.id)}
                        disabled={actioningId === req.id}
                        className="rounded-lg bg-red-600 px-3 py-1.5 text-xs font-medium text-white hover:bg-red-700 disabled:opacity-60"
                      >
                        Reject
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
  )
}