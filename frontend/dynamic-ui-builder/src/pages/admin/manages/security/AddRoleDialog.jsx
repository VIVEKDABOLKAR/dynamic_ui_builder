import React, { useState } from 'react'
import toast from 'react-hot-toast'
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
} from '@mui/material'
import { createRole } from '../../../../api/rolePermissionApi'

/**
 * "Add Role" dialog for ManageRolePermissions.
 *
 * Creates a `role` row an admin can then attach permission patterns to.
 * Note: this alone does NOT let anyone log in as the new role — that
 * still requires adding the matching value to the backend's
 * security.Role enum, since AppUser.role is a typed enum column. The
 * dialog surfaces that as a note so it isn't a surprise.
 */
export default function AddRoleDialog({ open, onClose, onCreated }) {
  const [code, setCode] = useState('')
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [saving, setSaving] = useState(false)

  const resetAndClose = () => {
    setCode('')
    setName('')
    setDescription('')
    setSaving(false)
    onClose()
  }

  const handleCreate = async () => {
    if (!code.trim()) {
      toast.error('Role code is required')
      return
    }

    setSaving(true)
    try {
      const role = await createRole({
        code: code.trim(),
        name: name.trim(),
        description: description.trim(),
      })
      toast.success(`Role ${role.code} created`)
      onCreated?.(role)
      resetAndClose()
    } catch (err) {
      toast.error(err?.response?.data?.message || err?.response?.data || 'Failed to create role')
      setSaving(false)
    }
  }

  return (
    <Dialog open={open} onClose={saving ? undefined : resetAndClose} fullWidth maxWidth="xs">
      <DialogTitle>Add Role</DialogTitle>
      <DialogContent className="flex flex-col gap-4 pt-2">
        <TextField
          label="Role code"
          placeholder="e.g. gate_operator"
          value={code}
          onChange={(e) => setCode(e.target.value)}
          helperText='Will be normalized to "ROLE_XXX" (e.g. ROLE_GATE_OPERATOR).'
          autoFocus
          fullWidth
        />
        <TextField
          label="Display name"
          placeholder="e.g. Gate Operator"
          value={name}
          onChange={(e) => setName(e.target.value)}
          fullWidth
        />
        <TextField
          label="Description (optional)"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          multiline
          minRows={2}
          fullWidth
        />
        <p className="text-xs text-slate-400">
          New roles have no permission patterns yet, so they'll be fail-open (access to every
          page) until you add patterns for them. Note: this only creates the role record — to
          actually assign it to a user, it also needs to be added to the backend's Role enum.
        </p>
      </DialogContent>
      <DialogActions className="px-6 pb-4">
        <Button onClick={resetAndClose} disabled={saving}>
          Cancel
        </Button>
        <Button onClick={handleCreate} disabled={saving} variant="contained">
          {saving ? 'Creating...' : 'Create Role'}
        </Button>
      </DialogActions>
    </Dialog>
  )
}
