import React, { useEffect, useState } from "react";
import {
  Button,
  Checkbox,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControlLabel,
  TextField,
  Typography,
} from "@mui/material";

const initialState = {
  lookupName: "",
  description: "",
  isActive: true,
};

export default function LookupMasterDialog({
  open,
  onClose,
  onSave,
  editingMaster,
}) {
  const [formData, setFormData] = useState(initialState);

  useEffect(() => {
    if (editingMaster) {
      setFormData({
        lookupName: editingMaster.lookupName || "",
        description: editingMaster.description || "",
        isActive: editingMaster.isActive ?? true,
      });
    } else {
      setFormData(initialState);
    }
  }, [editingMaster, open]);

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleCheckboxChange = (e) => {
    setFormData((prev) => ({
      ...prev,
      isActive: e.target.checked,
    }));
  };

  const handleSubmit = () => {
    onSave(formData);
  };

  return (
    <Dialog
      open={open}
      onClose={onClose}
      fullWidth
      maxWidth="sm"
    >
      <DialogTitle>
        {editingMaster ? "Edit Lookup Master" : "Create Lookup Master"}
      </DialogTitle>

      <DialogContent>

        <TextField
          margin="normal"
          fullWidth
          required
          label="Lookup Name"
          name="lookupName"
          value={formData.lookupName}
          onChange={handleChange}
        />

        <TextField
          margin="normal"
          fullWidth
          multiline
          rows={3}
          label="Description"
          name="description"
          value={formData.description}
          onChange={handleChange}
        />

        {editingMaster?.componentId != null && (
          <Typography
            variant="body2"
            color="text.secondary"
            sx={{ mt: 2 }}
          >
            This lookup is linked to Component ID{" "}
            <strong>{editingMaster.componentId}</strong>. Component mappings are
            managed by the system and cannot be changed.
          </Typography>
        )}

        <FormControlLabel
          sx={{ mt: 2 }}
          control={
            <Checkbox
              checked={formData.isActive}
              onChange={handleCheckboxChange}
            />
          }
          label="Active"
        />
      </DialogContent>

      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={onClose}>
          Cancel
        </Button>

        <Button
          variant="contained"
          onClick={handleSubmit}
        >
          {editingMaster ? "Update" : "Save"}
        </Button>
      </DialogActions>
    </Dialog>
  );
}