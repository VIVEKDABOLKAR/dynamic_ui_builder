import React, { useEffect, useState } from "react";
import {
  Alert,
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  TextField,
  Typography,
} from "@mui/material";

import { useFacility } from "../../../context/FacilityV2Context";
import { createFacilityAccessRequest, getFacilities } from "../../../api/facilityApi";
import FacilityAccessDialog from "./FacilityAccessDialog";

export default function FacilityListComponent() {
  const {
    facilities = [],
    selectedFacility,
    changeFacility,
    loading,
  } = useFacility();

  const [requestDialogOpen, setRequestDialogOpen] = useState(false);




  /**
   * Open request dialog.
   */
  const handleOpenRequestDialog = async () => {
    setRequestDialogOpen(true);

  };

  /**
   * Close request dialog.
   */
  const handleCloseDialog = () => {
    if (submitting) return;

    setRequestDialogOpen(false);

    setRequestData({
      facilityId: "",
      reason: "",
    });

    setError("");
  };

  /**
   * Handle normal facility selection.
   */
  const handleFacilityChange = (event) => {
    const value = event.target.value;

    if (value === "REQUEST_ACCESS") {
      handleOpenRequestDialog();
      return;
    }

    changeFacility(value);
  };

  /**
   * Handle request form changes.
   */
  const handleRequestChange = (event) => {
    const { name, value } = event.target;

    setRequestData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  /**
   * Submit facility access request.
   */
  const handleSubmitRequest = async () => {
    if (!requestData.facilityId || !requestData.reason.trim()) {
      return;
    }

    try {
      setSubmitting(true);
      setError("");

      const data = await createFacilityAccessRequest(requestData)

      handleCloseDialog();
    } catch (err) {
      console.error("Facility access request failed:", err);
      setError(
        err.message || "Unable to submit request. Please try again."
      );
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center gap-2">
        <CircularProgress size={18} />
        <Typography variant="body2" color="text.secondary">
          Loading facilities...
        </Typography>
      </div>
    );
  }

  return (
    <>
      <FormControl fullWidth size="small">
        <InputLabel id="facility-select-label">
          Facility
        </InputLabel>

        <Select
          labelId="facility-select-label"
          value={selectedFacility?.id || ""}
          label="Facility"
          onChange={handleFacilityChange}
        >
          {/* Facilities user already has access to */}
          {facilities.map((facility) => (
            <MenuItem key={facility.id} value={facility.id}>
              {facility.name}
            </MenuItem>
          ))}

          {/* Request access */}
          <MenuItem
            value="REQUEST_ACCESS"
            sx={{
              color: "primary.main",
              fontWeight: 600,
              borderTop: "1px solid",
              borderColor: "divider",
              mt: 1,
            }}
          >
            + Request Facility Access
          </MenuItem>
        </Select>
      </FormControl>

      <div>
        <FacilityAccessDialog  requestDialogOpen={requestDialogOpen} setRequestDialogOpen={setRequestDialogOpen}/>
      </div>
    </>
  );
}

