import React, { useEffect, useState } from "react";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  MenuItem,
  TextField,
  Typography,
} from "@mui/material";

const initialState = {
  code: "",
  name: "",
  description: "",
};

// Mirrors backend WorkflowStepType — step behaviour is coded per type, so
// the backend rejects any code outside this set. Keep in sync if the enum
// changes.
const KNOWN_STEP_CODES = [
  "GATE_CHECK_IN",
  "TRUCK_INSPECTION",
  "PARKING_ALLOCATION",
  "DOCK_ASSIGNMENT",
  "LOADING",
  "GATE_CHECK_OUT",
];

export default function WorkflowStepDialog({
  open,
  onClose,
  onSave,
  editingStep,
  existingCodes = [],
}) {
  const [formData, setFormData] = useState(initialState);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (editingStep) {
      setFormData({
        code: editingStep.code || "",
        name: editingStep.name || "",
        description: editingStep.description || "",
      });
    } else {
      setFormData(initialState);
    }
  }, [editingStep, open]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async () => {
    setSaving(true);
    try {
      await onSave(formData);
    } finally {
      setSaving(false);
    }
  };

  const availableCodes = KNOWN_STEP_CODES.filter(
    (c) => !existingCodes.includes(c)
  );

  const isValid = formData.code.trim() !== "" && formData.name.trim() !== "";

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>
        {editingStep ? "Edit Workflow Step" : "Add Workflow Step"}
      </DialogTitle>

      <DialogContent>
        {editingStep ? (
          <TextField
            margin="normal"
            fullWidth
            required
            disabled
            label="Step Code"
            name="code"
            value={formData.code}
            helperText="Step code cannot be changed once created."
          />
        ) : (
          <TextField
            margin="normal"
            fullWidth
            required
            select
            label="Step Code"
            name="code"
            value={formData.code}
            onChange={handleChange}
            helperText="Only codes with matching business logic can be added."
          >
            {availableCodes.length === 0 ? (
              <MenuItem disabled value="">
                All known step types already added
              </MenuItem>
            ) : (
              availableCodes.map((code) => (
                <MenuItem key={code} value={code}>
                  {code}
                </MenuItem>
              ))
            )}
          </TextField>
        )}

        <TextField
          margin="normal"
          fullWidth
          required
          label="Display Name"
          name="name"
          value={formData.name}
          onChange={handleChange}
          helperText="Shown to admins in the workflow configuration."
        />

        <TextField
          margin="normal"
          fullWidth
          multiline
          rows={2}
          label="Description"
          name="description"
          value={formData.description}
          onChange={handleChange}
        />

        {!editingStep && (
          <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
            Adding a step here only creates it as master data. Use "Add to
            Workflow" below to actually include it in the active workflow.
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
          {saving ? "Saving..." : editingStep ? "Update" : "Save"}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
