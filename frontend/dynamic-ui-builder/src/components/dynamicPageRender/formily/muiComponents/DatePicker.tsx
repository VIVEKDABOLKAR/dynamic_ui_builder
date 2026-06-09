import React from "react";
import { TextField } from "@mui/material";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";

export default function Datepicker({
    value,
    onChange,
    label,
    disabled,
}: any) {
    console.log("date picker component is rendering")
    return (
        <DatePicker
            label={label}
            onChange={onChange}
            disabled={disabled}
            slotProps={{
                textField: {
                    fullWidth: true,
                    size: "small",
                },
            }}
        />
    );
}