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
  lookupValue: "",
  displayValue: "",
  sequenceNo: 1,
  isActive: true,
};

export default function LookupDialog({
  open,
  onClose,
  onSave,
  editingLookup,
  selectedMaster,
}) {
  const [formData, setFormData] = useState(initialState);

  useEffect(() => {
    if (editingLookup) {
      setFormData({
        lookupValue: editingLookup.lookupValue || "",
        displayValue: editingLookup.displayValue || "",
        sequenceNo: editingLookup.sequenceNo ?? 1,
        isActive: editingLookup.isActive ?? true,
      });
    } else {
      setFormData(initialState);
    }
  }, [editingLookup, open]);

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]:
        name === "sequenceNo"
          ? value === ""
            ? ""
            : Number(value)
          : value,
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
        {editingLookup ? "Edit Lookup Value" : "Add Lookup Value"}
      </DialogTitle>

      <DialogContent>

        {selectedMaster && (
          <Typography
            variant="body2"
            color="text.secondary"
            sx={{ mb: 2 }}
          >
            Lookup Master: <strong>{selectedMaster.lookupName}</strong>
          </Typography>
        )}

        <TextField
          margin="normal"
          fullWidth
          required
          label="Lookup Value"
          name="lookupValue"
          value={formData.lookupValue}
          onChange={handleChange}
          helperText="Value stored in the database (e.g. M, F, ACTIVE)"
        />

        <TextField
          margin="normal"
          fullWidth
          required
          label="Display Value"
          name="displayValue"
          value={formData.displayValue}
          onChange={handleChange}
          helperText="Text shown in the dropdown"
        />

        <TextField
          margin="normal"
          fullWidth
          type="number"
          label="Sequence Number"
          name="sequenceNo"
          value={formData.sequenceNo}
          onChange={handleChange}
          inputProps={{ min: 1 }}
        />

        <FormControlLabel
          sx={{ mt: 1 }}
          control={
            <Checkbox
              checked={formData.isActive}
              onChange={(e) =>
                setFormData((prev) => ({
                  ...prev,
                  isActive: e.target.checked,
                }))
              } />
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
          {editingLookup ? "Update" : "Save"}
        </Button>
      </DialogActions>
    </Dialog>
  );
}