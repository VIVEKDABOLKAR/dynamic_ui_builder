import React, { useEffect, useState } from 'react'
import toast from 'react-hot-toast'

import {
    getUsers,
    updateUserRole,
    getRoles
} from '../../../../api/userRoleApi'

export default function ManageUserRoles() {

    const [roles, setRoles] = useState([])
    const [users, setUsers] = useState([])
    const [loading, setLoading] = useState(true)
    const [search, setSearch] = useState('')
    const [pendingRoles, setPendingRoles] = useState({})
    const [savingUser, setSavingUser] = useState(null)

    // Fetch roles from database
    const loadRoles = () => {
        getRoles()
            .then((data) => {
                setRoles(data || [])
            })
            .catch(() => {
                toast.error('Failed to load roles')
            })
    }

    // Fetch users
    const loadUsers = () => {
        setLoading(true)

        getUsers()
            .then((data) => {
                setUsers(data || [])
            })
            .catch(() => {
                toast.error('Failed to load users')
            })
            .finally(() => {
                setLoading(false)
            })
    }

    useEffect(() => {
        loadUsers()
        loadRoles()
    }, [])

    const handleRoleSelect = (userId, role) => {
        setPendingRoles((prev) => ({
            ...prev,
            [userId]: role
        }))
    }

    const handleSave = async (user) => {

        const newRole =
            pendingRoles[user.id] ?? user.role

        if (newRole === user.role) {
            return
        }

        setSavingUser(user.id)

        try {

            await updateUserRole(
                user.id,
                newRole
            )

            toast.success(
                'User role updated successfully'
            )

            setUsers((prev) =>
                prev.map((u) =>
                    u.id === user.id
                        ? {
                            ...u,
                            role: newRole
                        }
                        : u
                )
            )

            setPendingRoles((prev) => {

                const next = {
                    ...prev
                }

                delete next[user.id]

                return next
            })

        } catch (err) {

            toast.error(
                err.response?.data?.message ||
                'Failed to update user role'
            )

        } finally {

            setSavingUser(null)

        }
    }

    const filtered = users.filter((user) =>
        user.username
            .toLowerCase()
            .includes(search.toLowerCase())
    )

    if (loading) {
        return <div>Loading users...</div>
    }

    return (
        <div className="space-y-5">

            <div>
                <h1 className="text-xl font-semibold text-slate-900">
                    User Role Management
                </h1>

                <p className="text-sm text-slate-500">
                    Manage users and assign their application roles.
                </p>
            </div>

            <input
                value={search}
                onChange={(e) =>
                    setSearch(e.target.value)
                }
                placeholder="Search users..."
                className="w-full max-w-sm rounded-lg border border-slate-200 px-3 py-2 text-sm"
            />

            <div className="overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">

                <table className="w-full text-sm">

                    <thead className="bg-slate-50 text-left text-xs font-semibold uppercase text-slate-500">

                        <tr>
                            <th className="px-5 py-3">
                                Username
                            </th>

                            <th className="px-5 py-3">
                                Current Role
                            </th>

                            <th className="px-5 py-3">
                                Change Role
                            </th>

                            <th className="px-5 py-3 text-right">
                                Action
                            </th>
                        </tr>

                    </thead>

                    <tbody className="divide-y divide-slate-100">

                        {filtered.map((user) => {

                            const selected =
                                pendingRoles[user.id] ??
                                user.role

                            const isDirty =
                                selected !== user.role

                            return (
                                <tr key={user.id}>

                                    <td className="px-5 py-3 font-medium text-slate-900">
                                        {user.username}
                                    </td>

                                    <td className="px-5 py-3 text-slate-600">
                                        {user.role?.replace(
                                            'ROLE_',
                                            ''
                                        )}
                                    </td>

                                    <td className="px-5 py-3">

                                        <select
                                            value={selected}
                                            onChange={(e) =>
                                                handleRoleSelect(
                                                    user.id,
                                                    e.target.value
                                                )
                                            }
                                            className="rounded-lg border border-slate-200 px-2 py-1.5 text-sm"
                                        >

                                            {roles.map((role) => (

                                                <option
                                                    key={role.id}
                                                    value={role.code}
                                                >
                                                    {role.code.replace(
                                                        'ROLE_',
                                                        ''
                                                    )}
                                                </option>

                                            ))}

                                        </select>

                                    </td>

                                    <td className="px-5 py-3 text-right">

                                        {isDirty && (

                                            <button
                                                type="button"
                                                onClick={() =>
                                                    handleSave(user)
                                                }
                                                disabled={
                                                    savingUser === user.id
                                                }
                                                className="rounded-lg bg-blue-800 px-3 py-1.5 text-xs font-medium text-white hover:bg-blue-900 disabled:opacity-60"
                                            >

                                                {savingUser === user.id
                                                    ? 'Saving...'
                                                    : 'Save'}

                                            </button>

                                        )}

                                    </td>

                                </tr>
                            )
                        })}

                    </tbody>

                </table>

            </div>

        </div>
    )
}