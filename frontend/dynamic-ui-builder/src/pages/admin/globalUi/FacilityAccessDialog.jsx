import React, { useEffect, useState } from 'react'
import { useFacility } from '../../../context/FacilityV2Context';
import { Alert, Button, CircularProgress, Dialog, DialogActions, DialogContent, DialogTitle, FormControl, InputLabel, MenuItem, Select, TextField, Typography } from '@mui/material';
import { createFacilityAccessRequest, getFacilities } from '../../../api/facilityApi';

export default function FacilityAccessDialog({
    requestDialogOpen,
    setRequestDialogOpen
}) {
     const {
        facilities = [],
        selectedFacility,
        changeFacility,
        loading,
      } = useFacility();
    
      const [availableFacilities, setAvailableFacilities] = useState([]);
      const [loadingAvailableFacilities, setLoadingAvailableFacilities] = useState(false);
    
      const [submitting, setSubmitting] = useState(false);
      const [error, setError] = useState("");
    
      const [requestData, setRequestData] = useState({
        facilityId: "",
        reason: "",
      });
    
      /**
       * Fetch facilities that the user does NOT already have access to.
       */
      const fetchAvailableFacilities = async () => {
        try {
          setLoadingAvailableFacilities(true);
          setError("");
    
          const data = await getFacilities();
    
          const userFacilityIds = new Set(facilities.map((facility) => String(facility.id)));
          const newavailableFacilities = (data || []).filter((facility) =>
            !userFacilityIds.has(String(facility.id))
          );
          console.log(newavailableFacilities)
    
          setAvailableFacilities(newavailableFacilities);
        } catch (err) {
          console.error("Failed to fetch available facilities:", err);
          setError("Unable to load facilities. Please try again.");
        } finally {
          setLoadingAvailableFacilities(false);
        }
      };

      useEffect(() => {
         fetchAvailableFacilities();
      }, [requestDialogOpen])
    
      /**
       * Open request dialog.
       */
      const handleOpenRequestDialog = async () => {
        setRequestDialogOpen(true);
    
        // Fetch only when the dialog is opened.
        await fetchAvailableFacilities();
    
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
          console.log(data)
    
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
    
    return (
        <>
            {/* Facility Access Request Dialog */}
            <Dialog
                open={requestDialogOpen}
                onClose={handleCloseDialog}
                fullWidth
                maxWidth="sm"
            >
                <DialogTitle>
                    Request Facility Access
                </DialogTitle>

                <DialogContent>
                    <Typography
                        variant="body2"
                        color="text.secondary"
                        sx={{ mb: 3 }}
                    >
                        Select the facility you need access to and provide a
                        reason for your request.
                    </Typography>

                    {error && (
                        <Alert severity="error" sx={{ mb: 2 }}>
                            {error}
                        </Alert>
                    )}

                    {/* Facility */}
                    <FormControl
                        fullWidth
                        margin="normal"
                        disabled={loadingAvailableFacilities || submitting}
                    >
                        <InputLabel id="request-facility-label">
                            Facility
                        </InputLabel>

                        <Select
                            labelId="request-facility-label"
                            name="facilityId"
                            value={requestData.facilityId}
                            label="Facility"
                            onChange={handleRequestChange}
                        >
                            {loadingAvailableFacilities ? (
                                <MenuItem disabled>
                                    Loading facilities...
                                </MenuItem>
                            ) : availableFacilities.length === 0 ? (
                                <MenuItem disabled>
                                    No facilities available
                                </MenuItem>
                            ) : (
                                availableFacilities.map((facility) => (
                                    <MenuItem
                                        key={facility.id}
                                        value={facility.id}
                                    >
                                        {facility.name}
                                    </MenuItem>
                                ))
                            )}
                        </Select>
                    </FormControl>

                    {/* Reason */}
                    <TextField
                        fullWidth
                        multiline
                        minRows={4}
                        label="Reason"
                        name="reason"
                        value={requestData.reason}
                        onChange={handleRequestChange}
                        margin="normal"
                        disabled={submitting}
                        placeholder="Why do you need access to this facility?"
                        inputProps={{
                            maxLength: 500,
                        }}
                        helperText={`${requestData.reason.length}/500`}
                    />
                </DialogContent>

                <DialogActions>
                    <Button
                        onClick={handleCloseDialog}
                        disabled={submitting}
                    >
                        Cancel
                    </Button>

                    <Button
                        variant="contained"
                        onClick={handleSubmitRequest}
                        disabled={
                            submitting ||
                            loadingAvailableFacilities ||
                            !requestData.facilityId ||
                            !requestData.reason.trim()
                        }
                    >
                        {submitting ? (
                            <>
                                <CircularProgress
                                    size={18}
                                    color="inherit"
                                    sx={{ mr: 1 }}
                                />
                                Submitting...
                            </>
                        ) : (
                            "Submit Request"
                        )}
                    </Button>
                </DialogActions>
            </Dialog>
        </>
    )
}
