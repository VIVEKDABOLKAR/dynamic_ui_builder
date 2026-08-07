import React, { useEffect, useState } from "react";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
  Typography,
} from "@mui/material";

const initialState = {
  id: "",
  name: "",
};

export default function FacilityDialog({
  open,
  onClose,
  onSave,
  editingFacility,
}) {
  const [formData, setFormData] = useState(initialState);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (editingFacility) {
      setFormData({
        id: editingFacility.id || "",
        name: editingFacility.name || "",
      });
    } else {
      setFormData(initialState);
    }
  }, [editingFacility, open]);

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      // Facility ID becomes the primary key — keep it screen-friendly
      // (upper-cased, no spaces) since the backend normalizes it anyway.
      [name]: name === "id" ? value.toUpperCase().replace(/\s+/g, "_") : value,
    }));
  };

  const handleSubmit = async () => {
    setSaving(true);
    try {
      await onSave(formData);
    } finally {
      setSaving(false);
    }
  };

  const isValid = formData.id.trim() !== "" && formData.name.trim() !== "";

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>
        {editingFacility ? "Edit Facility" : "Add New Facility"}
      </DialogTitle>

      <DialogContent>
        <TextField
          margin="normal"
          fullWidth
          required
          label="Facility ID"
          name="id"
          value={formData.id}
          onChange={handleChange}
          disabled={!!editingFacility}
          helperText={
            editingFacility
              ? "Facility ID cannot be changed once created."
              : "Unique code for this facility (e.g. YARD01). Cannot be changed later."
          }
        />

        <TextField
          margin="normal"
          fullWidth
          required
          label="Facility Name"
          name="name"
          value={formData.name}
          onChange={handleChange}
          helperText="Display name shown to users, e.g. in the facility switcher."
        />

        {!editingFacility && (
          <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
            Users will need to request access to this facility before they can
            select it, unless access is granted directly.
          </Typography>
        )}
      </DialogContent>

      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={onClose} disabled={saving}>
          Cancel
        </Button>

        <Button
          variant="contained"
          onClick={handleSubmit}
          disabled={!isValid || saving}
        >
          {saving ? "Saving..." : editingFacility ? "Update" : "Save"}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
